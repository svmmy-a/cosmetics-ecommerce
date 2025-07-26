package com.cosmetics.service;

import com.cosmetics.dto.CartItemDto;
import com.cosmetics.dto.CartSummaryDto;
import com.cosmetics.entity.Customer;
import java.util.List;

public interface CartService {
    List<CartItemDto> getCartItems();
    List<CartItemDto> addItemToCart(CartItemDto cartItemDto);
    List<CartItemDto> removeItemFromCart(int index);
    List<CartItemDto> updateItemQuantity(int index, int quantity);
    List<Object> getCartItemsForCustomer(Customer customer);
    void clearCartForCustomer(Customer customer);
    Double calculateTotalPrice();
    
    /**
     * Calculate cart summary including subtotal, delivery cost and total
     * @return cart summary object with calculated values
     */
    CartSummaryDto getCartSummary();
    
    /**
     * Calculate cart summary for a specific customer
     * @param customer the customer to calculate cart for
     * @return cart summary object with calculated values
     */
    CartSummaryDto getCartSummaryForCustomer(Customer customer);
}
