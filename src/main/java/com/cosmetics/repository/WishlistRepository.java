package com.cosmetics.repository;

import com.cosmetics.entity.Customer;
import com.cosmetics.entity.Product;
import com.cosmetics.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

    List<Wishlist> findByCustomer(Customer customer);
    
    Optional<Wishlist> findByCustomerAndProduct(Customer customer, Product product);
    
    void deleteByCustomerAndProduct(Customer customer, Product product);
}
