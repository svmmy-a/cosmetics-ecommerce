package com.cosmetics.controller;

import com.cosmetics.dto.CustomerDto;
import com.cosmetics.dto.InventoryDto;
import com.cosmetics.dto.ProductSupplierDto;
import com.cosmetics.entity.Admin;
import com.cosmetics.entity.Category;
import com.cosmetics.entity.Product;
import com.cosmetics.entity.ProductSupplier;
import com.cosmetics.entity.Supplier;
import com.cosmetics.entity.Warehouse;
import com.cosmetics.entity.Customer;
import com.cosmetics.entity.Order;
import com.cosmetics.service.AdminService;
import com.cosmetics.service.AnalyticsService;
import com.cosmetics.service.CategoryService;
import com.cosmetics.service.CustomerService;
import com.cosmetics.service.FileUploadService;
import com.cosmetics.service.InventoryService;
import com.cosmetics.service.OrderService;
import com.cosmetics.service.ProductService;
import com.cosmetics.service.ProductSupplierService;
import com.cosmetics.service.SupplierService;
import com.cosmetics.service.WarehouseService;

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
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/admin/products")
    public String showProducts(Model model) {
        List<Product> products = productService.findAllProducts();
        // Debug: Print image URLs
        for (Product product : products) {
            System.out.println("Product: " + product.getName() + " - Image URL: " + product.getImageUrl());
        }
        model.addAttribute("products", products);
        model.addAttribute("activePage", "products");
        addAdminNameToModel(model);
        return "admin/admin-products";
    }

    @GetMapping("/admin/products/add")
    public String showAddProductForm(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("activePage", "products");
        addAdminNameToModel(model);
        return "admin/admin-product-add";
    }

    @PostMapping("/admin/products/add")
    public String addProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("size") String size,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "isNew", defaultValue = "false") Boolean isNew,
            Model model) {
        
        try {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setSize(size);
            product.setIsNew(isNew);
            
            // handle image upload or URL
            String finalImageUrl = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                // file upload takes priority
                finalImageUrl = fileUploadService.saveFile(imageFile);
                System.out.println("File uploaded successfully. Image URL: " + finalImageUrl);
            } else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                // use provided URL as fallback
                finalImageUrl = imageUrl;
                System.out.println("Using provided URL: " + finalImageUrl);
            }
            
            if (finalImageUrl == null || finalImageUrl.trim().isEmpty()) {
                model.addAttribute("error", "Please provide either an image file or image URL.");
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("activePage", "products");
                addAdminNameToModel(model);
                return "admin/admin-product-add";
            }
            
            product.setImageUrl(finalImageUrl);
            
            // fetch and set the category using category service
            Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
            product.setCategory(category);
            
            // Use product service to save the product
            productService.saveProduct(product);
            model.addAttribute("message", "Product added successfully: " + name);
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", "Error adding product: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("activePage", "products");
            addAdminNameToModel(model);
            return "admin/admin-product-add";
        }
    }

    @GetMapping("/admin/products/edit")
    public String showEditProductForm(@RequestParam("id") Integer productId, Model model) {
        try {
            try {
                Product product = productService.findById(productId);
                model.addAttribute("product", product);
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("activePage", "products");
                addAdminNameToModel(model);
                return "admin/admin-product-edit";
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", "Product not found");
                return "redirect:/admin/products";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error loading product: " + e.getMessage());
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/admin/products/edit")
    public String updateProduct(
            @RequestParam("productId") Integer productId,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("size") String size,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "keepCurrentImage", defaultValue = "false") Boolean keepCurrentImage,
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "isNew", defaultValue = "false") Boolean isNew,
            Model model) {
        
        try {
            Product product;
            try {
                product = productService.findById(productId);
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", "Product not found");
                return "redirect:/admin/products";
            }
            
            String oldImageUrl = product.getImageUrl();
            
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setSize(size);
            product.setIsNew(isNew);
            
            // handle image update
            String finalImageUrl = oldImageUrl; // keep current by default
            
            if (imageFile != null && !imageFile.isEmpty()) {
                // new file upload takes priority
                finalImageUrl = fileUploadService.saveFile(imageFile);
                System.out.println("New file uploaded. Image URL: " + finalImageUrl);
                
                // delete old uploaded file if it exists and is in uploads directory
                if (oldImageUrl != null && oldImageUrl.startsWith("/uploads/")) {
                    try {
                        fileUploadService.deleteFile(oldImageUrl);
                        System.out.println("Deleted old image: " + oldImageUrl);
                    } catch (Exception e) {
                        System.out.println("Could not delete old image: " + e.getMessage());
                    }
                }
            } else if (imageUrl != null && !imageUrl.trim().isEmpty() && !keepCurrentImage) {
                // use provided URL if not keeping current image
                finalImageUrl = imageUrl;
                System.out.println("Using provided URL: " + finalImageUrl);
                
                // delete old uploaded file if it exists and is in uploads directory
                if (oldImageUrl != null && oldImageUrl.startsWith("/uploads/")) {
                    try {
                        fileUploadService.deleteFile(oldImageUrl);
                        System.out.println("Deleted old image: " + oldImageUrl);
                    } catch (Exception e) {
                        System.out.println("Could not delete old image: " + e.getMessage());
                    }
                }
            }
            
            product.setImageUrl(finalImageUrl);
            
            // fetch and set the category using category service
            Category category = categoryService.findCategoryById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
            product.setCategory(category);
            
            productService.saveProduct(product);
            model.addAttribute("message", "Product updated successfully: " + name);
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", "Error updating product: " + e.getMessage());
            try {
                Product product = productService.findById(productId);
                model.addAttribute("product", product);
                model.addAttribute("categories", categoryService.getAllCategories());
                model.addAttribute("activePage", "products");
                addAdminNameToModel(model);
                return "admin/admin-product-edit";
            } catch (IllegalArgumentException ex) {
                // if product not found, redirect to products list
                return "redirect:/admin/products";
            }
            
        }
    }

    @GetMapping("/admin/products/delete")
    public String deleteProduct(@RequestParam("id") Integer productId, Model model) {
        try {
            // use product service to get the product
            Product product;
            try {
                product = productService.findById(productId);
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", "Product not found");
                return "redirect:/admin/products";
            }
            
            String productName = product.getName();
            String imageUrl = product.getImageUrl();
            
            // use product service to delete the product
            productService.deleteProduct(productId);
            
            // delete associated uploaded image file if it exists
            if (imageUrl != null && imageUrl.startsWith("/uploads/")) {
                try {
                    fileUploadService.deleteFile(imageUrl);
                    System.out.println("Deleted product image: " + imageUrl);
                } catch (Exception e) {
                    System.out.println("Could not delete product image: " + e.getMessage());
                }
            }
            
            model.addAttribute("message", "Product deleted successfully: " + productName);
            return "redirect:/admin/products";
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting product: " + e.getMessage());
            return "redirect:/admin/products";
        }
    }

    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private FileUploadService fileUploadService;
    

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private SupplierService supplierService;
    
    @Autowired
    private ProductSupplierService productSupplierService;

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
            BigDecimal totalRevenue = analyticsService.calculateTotalRevenue();
            model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        } catch (Exception e) {
            model.addAttribute("totalRevenue", BigDecimal.ZERO);
            System.err.println("Error fetching total revenue: " + e.getMessage());
        }
        
        try {
            long totalOrders = analyticsService.countTotalOrders();
            model.addAttribute("totalOrders", totalOrders);
        } catch (Exception e) {
            model.addAttribute("totalOrders", 0);
            System.err.println("Error fetching total orders: " + e.getMessage());
        }
        
        try {
            double revenueGrowth = analyticsService.calculateRevenueGrowthPercentage();
            model.addAttribute("revenueGrowth", revenueGrowth);
            model.addAttribute("revenueGrowthFormatted", String.format("%.2f", revenueGrowth));
        } catch (Exception e) {
            model.addAttribute("revenueGrowth", 0.0);
            model.addAttribute("revenueGrowthFormatted", "0.00");
            System.err.println("Error fetching revenue growth: " + e.getMessage());
        }
        
        try {
            double ordersGrowth = analyticsService.calculateOrdersGrowthPercentage();
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
            long pendingDeliveries = analyticsService.countPendingDeliveries();
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
            List<Map<String, Object>> topSellingProducts = analyticsService.getTopSellingProducts();
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
            // use service to find transactions by product ID
            List<ProductSupplier> transactions = productSupplierService.getTransactionsByProduct(inventory.getProductId());
            List<ProductSupplierDto> transactionDtos = productSupplierService.convertToDtoList(transactions);
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
        List<Product> products = productService.findAllProducts();
        List<Warehouse> warehouses = warehouseService.findAll();
        List<Supplier> suppliers = supplierService.findAll();
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
        // use warehouse service to create new warehouse
        warehouseService.createWarehouse(name, location, null); // Contact info is not used in the entity
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
        // format contact info in the expected format for the service
        String contactInfo = email + "|" + phone;
        
        // use supplier service to create new supplier
        supplierService.createSupplier(name, contactInfo, address);
        model.addAttribute("message", "Supplier added successfully: " + name);
        return "redirect:/admin/inventory";
    }
    
    @GetMapping("/admin/sales-data")
    @ResponseBody
    public Map<String, Object> getSalesData(@RequestParam(value = "range", defaultValue = "last28days") String range) {
        Map<String, Object> response = new HashMap<>();
        try {
            // fetch sales data for the specified range
            List<Map<String, Object>> salesData = analyticsService.getSalesDataForRange(range);
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
            List<Map<String, Object>> productSalesData = analyticsService.getAllProductSalesData();
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
            return analyticsService.getTopSellingProductsByRange(range);
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
