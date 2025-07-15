package com.cosmetics.controller;

import com.cosmetics.dto.CustomerDto;
import com.cosmetics.dto.InventoryDto;
import com.cosmetics.dto.ProductSupplierDto;
import com.cosmetics.entity.Admin;
import com.cosmetics.entity.Product;
import com.cosmetics.entity.ProductSupplier;
import com.cosmetics.entity.Supplier;
import com.cosmetics.entity.Warehouse;
import com.cosmetics.entity.Customer;
import com.cosmetics.mapper.DtoMapper;
import com.cosmetics.repository.ProductRepository;
import com.cosmetics.repository.ProductSupplierRepository;
import com.cosmetics.repository.SupplierRepository;
import com.cosmetics.repository.WarehouseRepository;
import com.cosmetics.service.AdminService;
import com.cosmetics.service.CustomerService;
import com.cosmetics.service.InventoryService;
import com.cosmetics.entity.Order;
import com.cosmetics.service.OrderService;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private DtoMapper dtoMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private ProductSupplierRepository productSupplierRepository;

    private void addAdminNameToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            String email = authentication.getName();
            Optional<Admin> adminOptional = adminService.findByEmail(email);
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                model.addAttribute("adminName", admin.getName());
            } else {
                model.addAttribute("adminName", "Admin");
            }
        } else {
            model.addAttribute("adminName", "Admin");
        }
    }

    @GetMapping("/admin-login")
    public String showAdminLoginPage(HttpSession session) {
        // check for existing customer session and invalidate it
        if (session.getAttribute("loggedInUser") != null) {
            session.invalidate();
        }
        return "admin/admin-login";
    }

    // spring security handles the login process at /admin/login
    // no custom auth logic needed here

    @GetMapping("/admin-dashboard")
    public String showAdminDashboard(Model model) {
        addAdminNameToModel(model);
        model.addAttribute("activePage", "dashboard");
        
        // fetch KPI data w error handling
        try {
            BigDecimal totalRevenue = orderService.calculateTotalRevenue();
            model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        } catch (Exception e) {
            model.addAttribute("totalRevenue", BigDecimal.ZERO);
            System.err.println("Error fetching total revenue: " + e.getMessage());
        }
        
        try {
            long totalOrders = orderService.countTotalOrders();
            model.addAttribute("totalOrders", totalOrders);
        } catch (Exception e) {
            model.addAttribute("totalOrders", 0);
            System.err.println("Error fetching total orders: " + e.getMessage());
        }
        
        try {
            double revenueGrowth = orderService.calculateRevenueGrowthPercentage();
            model.addAttribute("revenueGrowth", revenueGrowth);
            model.addAttribute("revenueGrowthFormatted", String.format("%.2f", revenueGrowth));
        } catch (Exception e) {
            model.addAttribute("revenueGrowth", 0.0);
            model.addAttribute("revenueGrowthFormatted", "0.00");
            System.err.println("Error fetching revenue growth: " + e.getMessage());
        }
        
        try {
            double ordersGrowth = orderService.calculateOrdersGrowthPercentage();
            model.addAttribute("ordersGrowth", ordersGrowth);
            model.addAttribute("ordersGrowthFormatted", String.format("%.2f", ordersGrowth));
        } catch (Exception e) {
            model.addAttribute("ordersGrowth", 0.0);
            model.addAttribute("ordersGrowthFormatted", "0.00");
            System.err.println("Error fetching orders growth: " + e.getMessage());
        }
        
        try {
            long totalCustomers = customerService.countTotalCustomers();
            model.addAttribute("totalCustomers", totalCustomers);
        } catch (Exception e) {
            model.addAttribute("totalCustomers", 0);
            System.err.println("Error fetching total customers: " + e.getMessage());
        }
        
        try {
            long pendingDeliveries = orderService.countPendingDeliveries();
            model.addAttribute("pendingDeliveries", pendingDeliveries);
        } catch (Exception e) {
            model.addAttribute("pendingDeliveries", 0);
            System.err.println("Error fetching pending deliveries: " + e.getMessage());
        }
        
        // fetch low stock products w error handling
        try {
            List<InventoryDto> lowStockProducts = inventoryService.getLowStockProducts();
            // sort by quantity to prioritise critical (red) alerts over warning (orange) alerts
            if (lowStockProducts != null) {
                lowStockProducts.sort((p1, p2) -> Integer.compare(p1.getQuantity(), p2.getQuantity()));
            }
            model.addAttribute("lowStockProducts", lowStockProducts != null ? lowStockProducts : new ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("lowStockProducts", new ArrayList<>());
            System.err.println("Error fetching low stock products: " + e.getMessage());
        }
        
        // fetch to selling products w error handling
        try {
            List<Map<String, Object>> topSellingProducts = orderService.getTopSellingProducts();
            model.addAttribute("topSellingProducts", topSellingProducts != null ? topSellingProducts : new ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("topSellingProducts", new ArrayList<>());
            System.err.println("Error fetching top selling products: " + e.getMessage());
        }
        
        return "admin/admin-dashboard";
    }

    @GetMapping("/admin/inventory")
    public String showInventory(Model model) {
        List<InventoryDto> inventoryList = inventoryService.getAllInventory();
        Map<Integer, List<ProductSupplierDto>> historicalData = new HashMap<>();
        
        for (InventoryDto inventory : inventoryList) {
            // method to find by product ID exists or will be added
            List<ProductSupplier> transactions = productSupplierRepository.findByProduct_ProductId(inventory.getProductId());
            List<ProductSupplierDto> transactionDtos = transactions.stream()
                .map(dtoMapper::toProductSupplierDto)
                .collect(Collectors.toList());
            historicalData.put(inventory.getProductId(), transactionDtos);
        }
        
        model.addAttribute("inventoryList", inventoryList);
        model.addAttribute("historicalData", historicalData);
        model.addAttribute("activePage", "inventory");
        addAdminNameToModel(model);
        return "admin/admin-inventory";
    }

    @GetMapping("/admin/inventory/edit")
    public String showEditInventoryForm(@RequestParam("id") Integer id, Model model) {
        InventoryDto inventory = inventoryService.getInventoryById(id);
        model.addAttribute("inventory", inventory);
        model.addAttribute("activePage", "inventory");
        addAdminNameToModel(model);
        return "admin/admin-inventory-edit";
    }

    @PostMapping("/admin/inventory/edit")
    public String updateInventory(@RequestParam("id") Integer id, @RequestParam("quantity") Integer quantity, Model model) {
        InventoryDto updatedInventory = inventoryService.updateInventoryQuantity(id, quantity);
        model.addAttribute("message", "Inventory updated successfully for " + updatedInventory.getProductName());
        return "redirect:/admin/inventory";
    }

    @GetMapping("/admin/inventory/add")
    public String showAddInventoryForm(Model model) {
        List<Product> products = productRepository.findAll();
        List<Warehouse> warehouses = warehouseRepository.findAll();
        List<Supplier> suppliers = supplierRepository.findAll();
        model.addAttribute("products", products);
        model.addAttribute("warehouses", warehouses);
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("activePage", "inventory");
        addAdminNameToModel(model);
        return "admin/admin-inventory-add";
    }

    @PostMapping("/admin/inventory/add")
    public String addInventory(
            @RequestParam("productId") Integer productId,
            @RequestParam("warehouseId") Integer warehouseId,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("buyPrice") Double buyPrice,
            @RequestParam(value = "supplierId", required = false) Integer supplierId,
            Model model) {
        InventoryDto newInventory = inventoryService.addInventory(productId, warehouseId, quantity, buyPrice, supplierId);
        model.addAttribute("message", "Inventory added successfully for " + newInventory.getProductName());
        return "redirect:/admin/inventory";
    }

    @GetMapping("/admin/inventory/delete")
    public String deleteInventory(@RequestParam("id") Integer id, Model model) {
        InventoryDto deletedInventory = inventoryService.deleteInventory(id);
        model.addAttribute("message", "Inventory deleted successfully for " + deletedInventory.getProductName());
        return "redirect:/admin/inventory";
    }

    @GetMapping("/admin/warehouse/add")
    public String showAddWarehouseForm(Model model) {
        model.addAttribute("activePage", "inventory");
        addAdminNameToModel(model);
        return "admin/admin-warehouse-add";
    }

    @PostMapping("/admin/warehouse/add")
    public String addWarehouse(
            @RequestParam("name") String name,
            @RequestParam("location") String location,
            Model model) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(name);
        warehouse.setAddress(location);
        warehouseRepository.save(warehouse);
        model.addAttribute("message", "Warehouse added successfully: " + name);
        return "redirect:/admin/inventory";
    }

    @GetMapping("/admin/supplier/add")
    public String showAddSupplierForm(Model model) {
        model.addAttribute("activePage", "inventory");
        addAdminNameToModel(model);
        return "admin/admin-supplier-add";
    }

    @PostMapping("/admin/supplier/add")
    public String addSupplier(
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("address") String address,
            Model model) {
        Supplier supplier = new Supplier();
        supplier.setName(name);
        supplier.setPhone(phone);
        supplier.setEmail(email);
        supplier.setAddress(address);
        supplierRepository.save(supplier);
        model.addAttribute("message", "Supplier added successfully: " + name);
        return "redirect:/admin/inventory";
    }
    
    @GetMapping("/admin/sales-data")
    @ResponseBody
    public Map<String, Object> getSalesData(@RequestParam(value = "range", defaultValue = "last28days") String range) {
        Map<String, Object> response = new HashMap<>();
        try {
            // fetch sales data for the specified range
            List<Map<String, Object>> salesData = orderService.getSalesDataForRange(range);
            List<String> dates = new ArrayList<>();
            List<BigDecimal> sales = new ArrayList<>();
            List<Integer> itemsOrdered = new ArrayList<>();
            
            for (Map<String, Object> dataPoint : salesData) {
                dates.add((String) dataPoint.get("date"));
                sales.add((BigDecimal) dataPoint.get("sales"));
                itemsOrdered.add((Integer) dataPoint.get("itemsOrdered"));
            }
            
            response.put("dates", dates);
            response.put("sales", sales);
            response.put("itemsOrdered", itemsOrdered);
        } catch (Exception e) {
            response.put("error", "Failed to fetch sales data: " + e.getMessage());
            response.put("dates", new ArrayList<>());
            response.put("sales", new ArrayList<>());
            response.put("itemsOrdered", new ArrayList<>());
            System.err.println("Error fetching sales data: " + e.getMessage());
        }
        return response;
    }
    
    @GetMapping("/admin/product-sales-data")
    @ResponseBody
    public Map<String, Object> getProductSalesData() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> productSalesData = orderService.getAllProductSalesData();
            List<String> productNames = new ArrayList<>();
            List<Integer> unitsSold = new ArrayList<>();
            
            for (Map<String, Object> dataPoint : productSalesData) {
                productNames.add((String) dataPoint.get("productName"));
                unitsSold.add((Integer) dataPoint.get("unitsSold"));
            }
            
            response.put("productNames", productNames);
            response.put("unitsSold", unitsSold);
        } catch (Exception e) {
            response.put("error", "Failed to fetch product sales data: " + e.getMessage());
            response.put("productNames", new ArrayList<>());
            response.put("unitsSold", new ArrayList<>());
            System.err.println("Error fetching product sales data: " + e.getMessage());
        }
        return response;
    }
    
    @GetMapping("/admin/top-products-data")
    @ResponseBody
    public List<Map<String, Object>> getTopProductsData(@RequestParam(value = "range", defaultValue = "monthly") String range) {
        try {
            return orderService.getTopSellingProductsByRange(range);
        } catch (Exception e) {
            System.err.println("Error fetching top products data: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @GetMapping("/admin/orders")
    public String showOrders(Model model) {
        List<Order> ordersList = orderService.getAllOrders();
        model.addAttribute("ordersList", ordersList);
        model.addAttribute("activePage", "orders");
        addAdminNameToModel(model);
        return "admin/admin-orders";
    }

    @PostMapping("/admin/orders/updateStatus")
    public String updateOrderStatus(@RequestParam("id") Integer id, @RequestParam("status") String status, Model model) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
        orderService.updateOrderStatus(id, orderStatus);
        model.addAttribute("message", "Order status updated successfully for Order ID: " + id);
        return "redirect:/admin/orders";
    }
    
    @GetMapping("/admin/customers")
    public String showCustomers(Model model) {
        List<CustomerDto> customersList = customerService.getAllCustomersWithStats();
        model.addAttribute("customersList", customersList);
        model.addAttribute("activePage", "customers");
        addAdminNameToModel(model);
        return "admin/admin-customers";
    }
    
    @GetMapping("/admin/customers/view")
    public String viewCustomerDetails(@RequestParam("id") Integer id, Model model) {
        Customer customer = customerService.findById(id);
        List<Order> orders = orderService.findByCustomer(customer);
        model.addAttribute("customer", customer);
        model.addAttribute("orders", orders);
        model.addAttribute("activePage", "customers");
        addAdminNameToModel(model);
        return "admin/admin-customer-details";
    }
}
