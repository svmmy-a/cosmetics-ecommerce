package com.cosmetics.service.impl;

import com.cosmetics.dto.CartItemDto;
import com.cosmetics.dto.CartSummaryDto;
import com.cosmetics.dto.InventoryDto;
import com.cosmetics.entity.Customer;
import com.cosmetics.service.CartService;
import com.cosmetics.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {
    private List<CartItemDto> cartItems = new ArrayList<>();

    @Autowired
    private InventoryService inventoryService;

    @Override
    public List<CartItemDto> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    @Override
    public List<CartItemDto> addItemToCart(CartItemDto cartItemDto) {
        if (cartItemDto.getProductId() != null) {
            int requestedQuantity = cartItemDto.getQuantity();
            int maxAllowedQuantity = validateAndGetMaxAllowedQuantity(cartItemDto.getProductId());
            
            for (CartItemDto item : cartItems) {
                if (item.getProductId() != null && item.getProductId().equals(cartItemDto.getProductId())) {
                    int newQuantity = item.getQuantity() + requestedQuantity;
                    if (newQuantity <= maxAllowedQuantity) {
                        item.setQuantity(newQuantity);
                        return new ArrayList<>(cartItems);
                    } else {
                        throwQuantityException(newQuantity);
                    }
                }
            }
            
            if (requestedQuantity <= maxAllowedQuantity) {
                cartItems.add(cartItemDto);
            } else {
                throwQuantityException(requestedQuantity);
            }
        }
        return new ArrayList<>(cartItems);
    }

    @Override
    public List<CartItemDto> removeItemFromCart(int index) {
        if (index >= 0 && index < cartItems.size()) {
            cartItems.remove(index);
        }
        return new ArrayList<>(cartItems);
    }

    @Override
    public List<CartItemDto> updateItemQuantity(int index, int quantity) {
        if (index >= 0 && index < cartItems.size()) {
            CartItemDto item = cartItems.get(index);
            if (item.getProductId() != null) {
                int maxAllowedQuantity = validateAndGetMaxAllowedQuantity(item.getProductId());
                if (quantity <= maxAllowedQuantity) {
                    item.setQuantity(quantity);
                } else {
                    throwQuantityException(quantity);
                }
            }
        }
        return new ArrayList<>(cartItems);
    }

    /**
     * validates inventory for a product and returns the maximum allowed quantity
     * @param productId The ID of the product to check
     * @return maximum quantity that can be added based on inventory and limit
     * @throws IllegalStateException if no inventory is found for the product
     */
    private int validateAndGetMaxAllowedQuantity(Integer productId) {
        List<InventoryDto> inventoryList = inventoryService.getAllInventory().stream()
                .filter(inv -> inv.getProductId().equals(productId))
                .collect(Collectors.toList());
        
        if (!inventoryList.isEmpty()) {
            InventoryDto inventory = inventoryList.get(0); // use the first inventory we find for this product
            int availableQuantity = inventory.getQuantity();
            return Math.min(availableQuantity, 5); // limit to 5 or inventory, whichever is lower
        } else {
            throw new IllegalStateException("Sorry, we can't add to bag - you've reached the maximum quantity for this product.");
        }
    }
    
    /**
     * throws exception based on the requested quantity compared to the limit
     * @param requestedQuantity The quantity requested
     * @throws IllegalStateException with appropriate message based on the limit
     */
    private void throwQuantityException(int requestedQuantity) {
        if (requestedQuantity > 5) {
            throw new IllegalStateException("Sorry, you can only purchase up to 5 units of this product.");
        } else {
            throw new IllegalStateException("Sorry, we can't add to bag - you've reached the maximum quantity for this product.");
        }
    }

    @Override
    public List<Object> getCartItemsForCustomer(com.cosmetics.entity.Customer customer) {
        // For simplicity, return the current cart items as a List<Object>
        // In a real implementation, this would fetch cart items specific to the customer
        List<Object> customerCartItems = new ArrayList<>();
        customerCartItems.addAll(cartItems);
        return customerCartItems;
    }

    @Override
    public void clearCartForCustomer(com.cosmetics.entity.Customer customer) {
        // For simplicity, clear the entire cart
        // In a real implementation, this would clear only the cart items for the specific customer
        cartItems.clear();
    }

    @Override
    public Double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (CartItemDto item : cartItems) {
            // Assuming CartItemDto has getPrice() and getQuantity() methods
            // If price is not directly available, it might need to be fetched from ProductService/InventoryService
            // For now, assuming price is available in CartItemDto
            // Removed incorrect null check as getPrice() returns primitive double
            totalPrice += item.getPrice() * item.getQuantity();
        }
        return totalPrice;
    }
    
    @Override
    public CartSummaryDto getCartSummary() {
        List<CartItemDto> items = getCartItems();
        double subtotal = calculateTotalPrice();
        double deliveryCost = subtotal >= 40.00 ? 0.00 : 5.00;
        double total = subtotal + deliveryCost;
        
        return new CartSummaryDto(items, subtotal, deliveryCost, total);
    }
    
    @Override
    public CartSummaryDto getCartSummaryForCustomer(Customer customer) {
        // for simplicity, using the same implementation as non customer specific
        // in real implementation, this would fetch cart items specific to the customer
        return getCartSummary();
    }
}
