package com.cosmetics.controller;

import com.cosmetics.entity.Customer;
import com.cosmetics.service.CustomerService;
import com.cosmetics.service.ProductService;
import com.cosmetics.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    private Customer getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String email = authentication.getName();
            return customerService.findByEmail(email);
        }
        return null;
    }

    @GetMapping("/get")
    public ResponseEntity<List<Integer>> getWishlist() {
        Customer customer = getCurrentCustomer();
        if (customer == null) {
            return ResponseEntity.status(401).body(null);
        }
        List<Integer> productIds = wishlistService.getWishlistProductIds(customer);
        return ResponseEntity.ok(productIds);
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Boolean>> toggleWishlist(@RequestBody Map<String, Integer> request) {
        Customer customer = getCurrentCustomer();
        if (customer == null) {
            return ResponseEntity.status(401).body(null);
        }
        Integer productId = request.get("productId");
        if (productId == null) {
            return ResponseEntity.badRequest().body(null);
        }
        var product = productService.findById(productId);
        if (product == null) {
            return ResponseEntity.badRequest().body(null);
        }
        boolean isInWishlist = wishlistService.isProductInWishlist(customer, product);
        if (isInWishlist) {
            wishlistService.removeProductFromWishlist(customer, product);
        } else {
            wishlistService.addProductToWishlist(customer, product);
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("isInWishlist", !isInWishlist);
        return ResponseEntity.ok(response);
    }
}
