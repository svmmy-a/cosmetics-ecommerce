package com.cosmetics.controller;

import com.cosmetics.dto.CheckoutResultDto;
import com.cosmetics.entity.Customer;
import com.cosmetics.entity.Order;
import com.cosmetics.service.CheckoutService;
import com.cosmetics.service.CustomerService;
import com.cosmetics.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutWebController {

    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private CheckoutService checkoutService;
    
    @Autowired
    private OrderService orderService;

    /**
     * renders the checkout page for order summary.
     * @param model model to pass data to the view
     * @return template name for checkout page
     */
    @GetMapping("/checkout")
    public String showCheckout(Model model) {
        return "checkout";  // Thymeleaf template: src/main/resources/templates/checkout.html
    }
    
    /**
     * renders the payment page for processing payment.
     * @param model model to pass data to the view
     * @param session the HTTP session to retrieve logged in user
     * @return template name for payment page
     */
    @GetMapping("/payment")
    public String showPayment(Model model, HttpSession session) {
        Customer loggedInUser = (Customer) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            // fetch customer from the database to ensure the session is active
            Customer customerFromDb = customerService.findById(loggedInUser.getCustomerId());
            if (customerFromDb != null) {
                // get cart summary using service
                CheckoutResultDto result = checkoutService.calculateCartSummary(customerFromDb);
                model.addAttribute("savedAddresses", result.getSavedAddresses());
                model.addAttribute("subtotal", result.getSubtotal());
                model.addAttribute("deliveryCost", result.getDeliveryCost());
                model.addAttribute("total", result.getTotal());
            }
        }
        return "payment";  // Thymeleaf template: src/main/resources/templates/payment.html
    }

    @PostMapping("/payment")
    public String processPayment(
            @RequestParam(value = "addressId", required = false) Integer addressId,
            @RequestParam(value = "address-line", required = false) String addressLine,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "postcode", required = false) String postcode,
            @RequestParam(value = "country", required = false) String country,
            HttpSession session,
            Model model) {
        Customer loggedInUser = (Customer) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            // fetch customer from the database to ensure the session is active
            Customer customerFromDb = customerService.findById(loggedInUser.getCustomerId());
            if (customerFromDb != null) {
                // process payment through service
                Order order = checkoutService.processPayment(customerFromDb, addressId, addressLine, city, postcode, country);
                
                if (order != null) {
                    session.setAttribute("recentOrderId", order.getOrderId());
                    return "redirect:/payment-confirmation";  // head to confirmation page
                } else {
                    model.addAttribute("error", "Failed to create order.");
                    return "payment";
                }
            } else {
                model.addAttribute("error", "Customer not found in database.");
                return "payment";
            }
        } else {
            model.addAttribute("error", "User not logged in.");
            return "payment";
        }
    }

    @GetMapping("/payment-confirmation")
    public String showPaymentConfirmation(Model model, HttpSession session) {
        Integer recentOrderId = (Integer) session.getAttribute("recentOrderId");
        if (recentOrderId != null) {
            Order order = orderService.findById(recentOrderId);
            if (order != null && order.getOrderNumber() != null) {
                model.addAttribute("orderNumber", order.getOrderNumber());
            } else {
                model.addAttribute("orderNumber", "Unavailable (Order not found)");
            }
        } else {
            model.addAttribute("orderNumber", "Unavailable (No recent order)");
        }
        return "payment-confirmation";  // Thymeleaf template: src/main/resources/templates/payment-confirmation.html
    }
}
