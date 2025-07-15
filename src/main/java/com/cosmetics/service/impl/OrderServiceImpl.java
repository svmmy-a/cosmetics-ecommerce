package com.cosmetics.service.impl;

import com.cosmetics.entity.Address;
import com.cosmetics.entity.Customer;
import com.cosmetics.entity.Order;
import com.cosmetics.entity.OrderItem;
import com.cosmetics.entity.Product;
import com.cosmetics.repository.OrderItemRepository;
import com.cosmetics.repository.OrderRepository;
import com.cosmetics.repository.ProductRepository;
import com.cosmetics.service.CartService;
import com.cosmetics.service.InventoryService;
import com.cosmetics.service.OrderService;
import com.cosmetics.dto.InventoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryService inventoryService;

    @Override
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order createOrder(Customer customer, Address address, List<Object> cartItems) {
        List<OrderItem> orderItems = buildOrderItemsFromCart(cartItems);
        BigDecimal subtotal = calculateSubtotal(orderItems);
        BigDecimal deliveryFee = calculateDeliveryFee(subtotal);
        BigDecimal totalPrice = subtotal.add(deliveryFee);

        Order order = buildOrder(customer, address, totalPrice);
        Order savedOrder = orderRepository.save(order);

        String orderNumber = generateOrderNumber(savedOrder.getOrderId());
        savedOrder.setOrderNumber(orderNumber);

        updateInventoryAndSaveOrderItems(savedOrder, orderItems);
        return orderRepository.save(savedOrder);
    }

    private List<OrderItem> buildOrderItemsFromCart(List<Object> cartItems) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (Object item : cartItems) {
            if (item instanceof com.cosmetics.dto.CartItemDto) {
                com.cosmetics.dto.CartItemDto cartItem = (com.cosmetics.dto.CartItemDto) item;
                Integer productId = cartItem.getProductId();
                Integer quantity = cartItem.getQuantity();
                if (productId != null && quantity != null) {
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new IllegalStateException("Product not found with ID: " + productId));
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(product);
                    orderItem.setQuantity(quantity);
                    orderItem.setPrice(product.getPrice());
                    orderItems.add(orderItem);
                }
            }
        }
        return orderItems;
    }

    private BigDecimal calculateSubtotal(List<OrderItem> orderItems) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            BigDecimal price = orderItem.getPrice();
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(orderItem.getQuantity()));
            subtotal = subtotal.add(itemTotal);
        }
        return subtotal;
    }

    private BigDecimal calculateDeliveryFee(BigDecimal subtotal) {
        return subtotal.compareTo(BigDecimal.valueOf(40.00)) < 0 ? BigDecimal.valueOf(5.00) : BigDecimal.ZERO;
    }

    private Order buildOrder(Customer customer, Address address, BigDecimal totalPrice) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setAddress(address);
        order.setTotalPrice(totalPrice);
        return order;
    }

    private void updateInventoryAndSaveOrderItems(Order savedOrder, List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(savedOrder);
            orderItemRepository.save(orderItem);
            savedOrder.addOrderItem(orderItem);
            
            Product product = orderItem.getProduct();
            Integer quantityOrdered = orderItem.getQuantity();
            List<InventoryDto> inventoryList = inventoryService.getAllInventory().stream()
                    .filter(inv -> inv.getProductId().equals(product.getProductId()))
                    .collect(Collectors.toList());
            
            if (!inventoryList.isEmpty()) {
                InventoryDto inventory = inventoryList.get(0);
                Integer currentQuantity = inventory.getQuantity();
                if (currentQuantity >= quantityOrdered) {
                    inventoryService.updateInventoryQuantity(inventory.getInventoryId(), currentQuantity - quantityOrdered);
                } else {
                    throw new IllegalStateException("Insufficient stock for product: " + product.getName());
                }
            } else {
                throw new IllegalStateException("No inventory record found for product: " + product.getName());
            }
        }
    }

    private String generateOrderNumber(Integer orderId) {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("ORD-%s-%05d", datePart, orderId);
    }

    @Override
    public Order findById(Integer orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Override
    public List<Order> findByCustomer(Customer customer) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getCustomer().getCustomerId().equals(customer.getCustomerId()))
                .collect(Collectors.toList());
    }

    @Override
    public Order updateOrderStatus(Integer orderId, Order.OrderStatus status) {
        Order order = findById(orderId);
        if (order != null) {
            order.setOrderStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }

    @Override
    public BigDecimal calculateTotalRevenue() {
        List<Order> allOrders = orderRepository.findAll();
        return allOrders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public long countTotalOrders() {
        return orderRepository.count();
    }

    @Override
    public long countPendingDeliveries() {
        return orderRepository.findAll().stream()
                .filter(order -> order.getOrderStatus() == Order.OrderStatus.pending)
                .count();
    }

    @Override
    public List<Map<String, Object>> getTopSellingProducts() {
        return getTopSellingProductsByRange("monthly");
    }
    
    @Override
    public List<Map<String, Object>> getTopSellingProductsByRange(String range) {
        LocalDateTime startDateTime;
        switch (range) {
            case "weekly":
                startDateTime = LocalDateTime.now().minusDays(7);
                break;
            case "monthly":
            default:
                startDateTime = LocalDateTime.now().minusDays(28);
                break;
        }
        
        List<OrderItem> filteredOrderItems = orderItemRepository.findAll().stream()
                .filter(item -> item.getOrder().getOrderDate() != null && !item.getOrder().getOrderDate().isBefore(startDateTime))
                .collect(Collectors.toList());
                
        Map<Product, Integer> productSales = new HashMap<>();
        for (OrderItem item : filteredOrderItems) {
            Product product = item.getProduct();
            productSales.merge(product, item.getQuantity(), Integer::sum);
        }
        
        List<Map<String, Object>> topProducts = new ArrayList<>();
        productSales.entrySet().stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    Map<String, Object> productData = new HashMap<>();
                    Product product = entry.getKey();
                    productData.put("productName", product.getName());
                    productData.put("unitsSold", entry.getValue());
                    productData.put("category", product.getCategory() != null ? product.getCategory().getName() : "Uncategorized");
                    // Fetch actual inventory quantity
                    Integer inventoryQty = 0;
                    List<InventoryDto> inventoryList = inventoryService.getAllInventory().stream()
                            .filter(inv -> inv.getProductId().equals(product.getProductId()))
                            .collect(Collectors.toList());
                    if (!inventoryList.isEmpty()) {
                        inventoryQty = inventoryList.get(0).getQuantity();
                    }
                    productData.put("inventoryQty", inventoryQty);
                    // calculate earnings
                    BigDecimal earnings = product.getPrice().multiply(BigDecimal.valueOf(entry.getValue()));
                    productData.put("earnings", earnings);
                    // Add image URL from Product entity
                    productData.put("imageUrl", product.getImageUrl() != null ? product.getImageUrl() : "/assets/product1.jpg");
                    // Add description and size from Product entity
                    productData.put("description", product.getDescription() != null ? product.getDescription() : "Description not available");
                    productData.put("size", product.getSize() != null ? product.getSize() : "N/A");
                    productData.put("productId", product.getProductId());
                    topProducts.add(productData);
                });
                
        return topProducts;
    }
    
    @Override
    public List<Map<String, Object>> getAllProductSalesData() {
        List<OrderItem> allOrderItems = orderItemRepository.findAll();
        Map<Product, Integer> productSales = new HashMap<>();
        
        for (OrderItem item : allOrderItems) {
            Product product = item.getProduct();
            productSales.merge(product, item.getQuantity(), Integer::sum);
        }
        
        List<Map<String, Object>> allProducts = new ArrayList<>();
        productSales.entrySet().stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    Map<String, Object> productData = new HashMap<>();
                    productData.put("productName", entry.getKey().getName());
                    productData.put("unitsSold", entry.getValue());
                    allProducts.add(productData);
                });
                
        return allProducts;
    }
    
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    @Override
    public List<Map<String, Object>> getSalesDataLast30Days() {
        return getSalesDataForRange("last28days");
    }
    
    @Override
    public BigDecimal calculateRevenueForCurrentMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return calculateRevenueForPeriod(startOfMonth, LocalDateTime.now());
    }
    
    @Override
    public BigDecimal calculateRevenueForPreviousMonth() {
        LocalDateTime startOfPreviousMonth = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfPreviousMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0).minusSeconds(1);
        return calculateRevenueForPeriod(startOfPreviousMonth, endOfPreviousMonth);
    }
    
    private BigDecimal calculateRevenueForPeriod(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> order.getOrderDate() != null && !order.getOrderDate().isBefore(start) && !order.getOrderDate().isAfter(end))
                .collect(Collectors.toList());
        return orders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public long countOrdersForCurrentMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return countOrdersForPeriod(startOfMonth, LocalDateTime.now());
    }
    
    @Override
    public long countOrdersForPreviousMonth() {
        LocalDateTime startOfPreviousMonth = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfPreviousMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0).minusSeconds(1);
        return countOrdersForPeriod(startOfPreviousMonth, endOfPreviousMonth);
    }
    
    private long countOrdersForPeriod(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getOrderDate() != null && !order.getOrderDate().isBefore(start) && !order.getOrderDate().isAfter(end))
                .count();
    }
    
    @Override
    public double calculateRevenueGrowthPercentage() {
        BigDecimal currentRevenue = calculateRevenueForCurrentMonth();
        BigDecimal previousRevenue = calculateRevenueForPreviousMonth();
        if (previousRevenue.compareTo(BigDecimal.ZERO) == 0) {
            return currentRevenue.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        BigDecimal change = currentRevenue.subtract(previousRevenue);
        return change.divide(previousRevenue, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
    
    @Override
    public double calculateOrdersGrowthPercentage() {
        long currentOrders = countOrdersForCurrentMonth();
        long previousOrders = countOrdersForPreviousMonth();
        if (previousOrders == 0) {
            return currentOrders > 0 ? 100.0 : 0.0;
        }
        double change = currentOrders - previousOrders;
        return (change / previousOrders) * 100;
    }
    
    public List<Map<String, Object>> getSalesDataForRange(String range) {
        LocalDateTime startDateTime;
        int weeks;
        switch (range) {
            case "today":
                startDateTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
                weeks = 1;
                break;
            case "last7days":
                startDateTime = LocalDateTime.now().minusDays(7);
                weeks = 1;
                break;
            case "last3months":
                startDateTime = LocalDateTime.now().minusMonths(3);
                weeks = 12;
                break;
            case "last28days":
            default:
                startDateTime = LocalDateTime.now().minusDays(28);
                weeks = 4;
                break;
        }
        
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> order.getOrderDate() != null && !order.getOrderDate().isBefore(startDateTime))
                .collect(Collectors.toList());
        
        // create weekly data for the specified number of weeks
        List<Map<String, Object>> weeklyData = new ArrayList<>();
        LocalDate endDate = LocalDate.now();
        for (int i = 0; i < weeks; i++) {
            LocalDate startOfWeek = endDate.minusDays(6); // mon - sun (7 days)
            Map<String, Object> weekData = new HashMap<>();
            String label = startOfWeek.getMonthValue() + "-" + startOfWeek.getDayOfMonth() + " to " + 
                          endDate.getMonthValue() + "-" + endDate.getDayOfMonth();
            weekData.put("date", label);
            weekData.put("sales", BigDecimal.ZERO);
            weekData.put("itemsOrdered", 0);
            weeklyData.add(0, weekData); // oldest week first
            endDate = startOfWeek.minusDays(1); // move to previous sunday
        }
        
        // sales and items ordered by week
        for (Order order : orders) {
            if (order.getOrderDate() != null) {
                LocalDate orderDate = order.getOrderDate().toLocalDate();
                for (Map<String, Object> weekData : weeklyData) {
                    String label = (String) weekData.get("date");
                    String[] parts = label.split(" to ");
                    String[] startParts = parts[0].split("-");
                    String[] endParts = parts[1].split("-");
                    LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), Integer.parseInt(startParts[0]), Integer.parseInt(startParts[1]));
                    LocalDate endOfWeek = LocalDate.of(LocalDate.now().getYear(), Integer.parseInt(endParts[0]), Integer.parseInt(endParts[1]));
                    
                    if (!orderDate.isBefore(startDate) && !orderDate.isAfter(endOfWeek)) {
                        BigDecimal currentSales = (BigDecimal) weekData.get("sales");
                        int currentItems = (Integer) weekData.get("itemsOrdered");
                        BigDecimal orderTotal = order.getTotalPrice() != null ? order.getTotalPrice() : BigDecimal.ZERO;
                        int totalUnits = 0;
                        if (order.getOrderItems() != null) {
                            for (OrderItem item : order.getOrderItems()) {
                                totalUnits += item.getQuantity();
                            }
                        }
                        
                        weekData.put("sales", currentSales.add(orderTotal));
                        weekData.put("itemsOrdered", currentItems + totalUnits);
                        break;
                    }
                }
            }
        }
        
        return weeklyData;
    }

    @Override
    public long getOrderCountByCustomerId(Integer customerId) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getCustomer() != null && order.getCustomer().getCustomerId().equals(customerId))
                .count();
    }

    @Override
    public double getTotalSpendByCustomerId(Integer customerId) {
        List<Order> customerOrders = orderRepository.findAll().stream()
                .filter(order -> order.getCustomer() != null && order.getCustomer().getCustomerId().equals(customerId))
                .collect(Collectors.toList());
        double totalSpend = customerOrders.stream()
                .map(Order::getTotalPrice)
                .filter(totalPrice -> totalPrice != null)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
        // round to 2 decimal places
        return BigDecimal.valueOf(totalSpend)
                .setScale(2, java.math.RoundingMode.HALF_UP)
                .doubleValue();
    }

    @Override
    public LocalDate getLastOrderDateByCustomerId(Integer customerId) {
        LocalDateTime lastOrderDateTime = orderRepository.findAll().stream()
                .filter(order -> order.getCustomer() != null && order.getCustomer().getCustomerId().equals(customerId))
                .map(Order::getOrderDate)
                .filter(orderDate -> orderDate != null)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        // return date as localtime
        return lastOrderDateTime != null ? lastOrderDateTime.toLocalDate() : null;
    }
}
