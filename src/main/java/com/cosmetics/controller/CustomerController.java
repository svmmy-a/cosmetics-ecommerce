package com.cosmetics.controller;

import com.cosmetics.entity.Customer;
import com.cosmetics.entity.Order;
import com.cosmetics.entity.OrderItem;
import com.cosmetics.entity.Product;
import com.cosmetics.entity.ProductRating;
import com.cosmetics.service.CustomerService;
import com.cosmetics.service.OrderService;
import com.cosmetics.service.ProductService;
import com.cosmetics.service.ProductRatingService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRatingService productRatingService;

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerCustomer(@ModelAttribute("customer") Customer customer, Model model) {
        try {
            if (customer.getFullName() == null || customer.getFullName().trim().isEmpty() ||
                customer.getEmail() == null || customer.getEmail().trim().isEmpty() ||
                customer.getPassword() == null || customer.getPassword().trim().isEmpty()) {
                model.addAttribute("error", "All required fields must be filled.");
                return "signup";
            }
            customerService.registerCustomer(customer);
            return "redirect:/login?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
        try {
            Customer customer = customerService.authenticate(email, password);
            session.setAttribute("loggedInUser", customer);
            session.setAttribute("loginSuccess", true);
            return "redirect:/index";
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logoutSuccess=true";
    }

    @GetMapping("/order-history")
    public String showOrderHistory(Model model, HttpSession session) {
        Customer customer = (Customer) session.getAttribute("loggedInUser");
        if (customer == null) {
            return "redirect:/login?redirect=/customer/order-history";
        }
        List<Order> orders = orderService.findByCustomer(customer);
        // Add information about existing reviews for each product
        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                boolean hasReviewed = productRatingService.hasReviewed(customer, item.getProduct());
                item.setHasReviewed(hasReviewed);
                if (hasReviewed) {
                    ProductRating existingReview = productRatingService.findByCustomerAndProduct(customer, item.getProduct());
                    if (existingReview != null) {
                        item.setExistingRating(existingReview.getRating());
                        item.setExistingComment(existingReview.getComment());
                    }
                }
            }
        }
        model.addAttribute("orders", orders);
        return "order-history";
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model) {
        return "checkout";
    }

    @PostMapping("/clearLoginSuccess")
    public String clearLoginSuccess(HttpSession session) {
        session.removeAttribute("loginSuccess");
        return "redirect:/index";
    }

    @PostMapping("/submit-review")
    public String submitReview(Model model, HttpSession session, 
                              @RequestParam("productId") Integer productId, 
                              @RequestParam("rating") int rating, 
                              @RequestParam("comment") String comment) {
        Customer customer = (Customer) session.getAttribute("loggedInUser");
        if (customer == null) {
            return "redirect:/login?redirect=/customer/order-history";
        }
        try {
            Product product = productService.findById(productId);
            if (product == null) {
                throw new IllegalArgumentException("Product not found.");
            }
            productRatingService.submitReview(customer, product, rating, comment);
            return "redirect:/customer/order-history?success=true";
        } catch (IllegalStateException | IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/customer/order-history?error=" + e.getMessage().replace(" ", "+");
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred.");
            return "redirect:/customer/order-history?error=UnexpectedError";
        }
    }
}
