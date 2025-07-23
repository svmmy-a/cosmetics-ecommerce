package com.cosmetics.service;

import com.cosmetics.dto.CartItemDto;
import java.util.List;

public interface CartService {
    List<CartItemDto> getCartItems();
    List<CartItemDto> addItemToCart(CartItemDto cartItemDto);
    List<CartItemDto> removeItemFromCart(int index);
    List<CartItemDto> updateItemQuantity(int index, int quantity);
    List<Object> getCartItemsForCustomer(com.cosmetics.entity.Customer customer);
    void clearCartForCustomer(com.cosmetics.entity.Customer customer);
    Double calculateTotalPrice();
}
