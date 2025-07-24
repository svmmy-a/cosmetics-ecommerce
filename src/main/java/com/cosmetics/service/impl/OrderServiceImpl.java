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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the OrderService interface.
 * Handles core order processing operations.
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartService cartService,
            ProductRepository productRepository,
            InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    @Override
    @Transactional
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    @Transactional
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
    @Transactional
    public Order updateOrderStatus(Integer orderId, Order.OrderStatus status) {
        Order order = findById(orderId);
        if (order != null) {
            order.setOrderStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }
    
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
