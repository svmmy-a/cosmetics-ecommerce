package com.cosmetics.service.impl;

import com.cosmetics.entity.Customer;
import com.cosmetics.entity.Product;
import com.cosmetics.entity.Wishlist;
import com.cosmetics.repository.WishlistRepository;
import com.cosmetics.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Override
    public List<Wishlist> getWishlistByCustomer(Customer customer) {
        return wishlistRepository.findByCustomer(customer);
    }

    @Override
    public boolean isProductInWishlist(Customer customer, Product product) {
        return wishlistRepository.findByCustomerAndProduct(customer, product).isPresent();
    }

    @Override
    public Wishlist addProductToWishlist(Customer customer, Product product) {
        Optional<Wishlist> existingWishlist = wishlistRepository.findByCustomerAndProduct(customer, product);
        if (existingWishlist.isPresent()) {
            return existingWishlist.get();
        }
        Wishlist wishlist = new Wishlist(customer, product);
        return wishlistRepository.save(wishlist);
    }

    @Override
    public void removeProductFromWishlist(Customer customer, Product product) {
        wishlistRepository.deleteByCustomerAndProduct(customer, product);
    }

    @Override
    public List<Integer> getWishlistProductIds(Customer customer) {
        return wishlistRepository.findByCustomer(customer)
                .stream()
                .map(wishlist -> wishlist.getProduct().getProductId())
                .collect(Collectors.toList());
    }
}
