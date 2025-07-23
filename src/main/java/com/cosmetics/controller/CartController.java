package com.cosmetics.controller;

import com.cosmetics.dto.CartItemDto;
import com.cosmetics.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItemDto>> getCartItems() {
        return ResponseEntity.ok(cartService.getCartItems());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addItemToCart(@RequestBody CartItemDto cartItemDto) {
        try {
            return ResponseEntity.ok(cartService.addItemToCart(cartItemDto));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/remove/{index}")
    public ResponseEntity<List<CartItemDto>> removeItemFromCart(@PathVariable int index) {
        return ResponseEntity.ok(cartService.removeItemFromCart(index));
    }

    @PutMapping("/update/{index}")
    public ResponseEntity<?> updateItemQuantity(@PathVariable int index, @RequestParam int quantity) {
        try {
            return ResponseEntity.ok(cartService.updateItemQuantity(index, quantity));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/total")
    public ResponseEntity<Double> getTotalCartPrice() {
        return ResponseEntity.ok(cartService.calculateTotalPrice());
    }
}
