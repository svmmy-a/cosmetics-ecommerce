package com.cosmetics.service;

import com.cosmetics.entity.Customer;
import com.cosmetics.entity.Product;
import com.cosmetics.entity.Wishlist;

import java.util.List;

public interface WishlistService {

    List<Wishlist> getWishlistByCustomer(Customer customer);

    boolean isProductInWishlist(Customer customer, Product product);

    Wishlist addProductToWishlist(Customer customer, Product product);

    void removeProductFromWishlist(Customer customer, Product product);

    List<Integer> getWishlistProductIds(Customer customer);
}
