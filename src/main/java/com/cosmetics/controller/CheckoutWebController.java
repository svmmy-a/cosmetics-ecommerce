package com.cosmetics.controller;

import com.cosmetics.entity.Address;
import com.cosmetics.entity.Customer;
import com.cosmetics.entity.Order;
import com.cosmetics.service.CartService;
import com.cosmetics.service.CustomerService;
import com.cosmetics.service.OrderService;
import java.util.List;
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
    private OrderService orderService;
    
    @Autowired
    private CartService cartService;

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
                model.addAttribute("savedAddresses", customerFromDb.getAddresses());
                // Calculate and add cart summary details to display on payment page
                List<Object> cartItems = new java.util.ArrayList<>(cartService.getCartItems());
                double subtotal = cartItems.stream()
                    .mapToDouble(item -> {
                        com.cosmetics.dto.CartItemDto cartItem = (com.cosmetics.dto.CartItemDto) item;
                        return cartItem.getPrice() * cartItem.getQuantity();
                    })
                    .sum();
                double deliveryCost = subtotal >= 40.00 ? 0.00 : 5.00;
                double total = subtotal + deliveryCost;
                model.addAttribute("subtotal", subtotal);
                model.addAttribute("deliveryCost", deliveryCost);
                model.addAttribute("total", total);
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
                Address addressToUse;
                if (addressId != null) {
                    // use existing address
                    addressToUse = customerFromDb.getAddresses().stream()
                        .filter(a -> a.getAddressId().equals(addressId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Selected address not found"));
                } else {
                    // check if the new address already exists to avoid duplicates
                    addressToUse = customerFromDb.getAddresses().stream()
                        .filter(a -> a.getAddressLine().equals(addressLine) && a.getCity().equals(city)
                            && a.getPostcode().equals(postcode) && a.getCountry().equals(country))
                        .findFirst()
                        .orElseGet(() -> {
                            // create new address if no duplicate is found
                            Address newAddress = new Address(customerFromDb, addressLine, city, postcode, country);
                            customerFromDb.addAddress(newAddress);
                            customerService.save(customerFromDb);
                            // ensure the address is persisted and retrieve it with ID
                            return customerFromDb.getAddresses().stream()
                                .filter(a -> a.getAddressLine().equals(addressLine) && a.getCity().equals(city)
                                    && a.getPostcode().equals(postcode) && a.getCountry().equals(country))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("New address not persisted"));
                        });
                }
                
                // create order with cart items
                List<Object> cartItems = cartService.getCartItemsForCustomer(customerFromDb);
                Order order = orderService.createOrder(customerFromDb, addressToUse, cartItems);
                if (order != null) {
                    session.setAttribute("recentOrderId", order.getOrderId());
                    cartService.clearCartForCustomer(customerFromDb); // clear cart after successful order
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
