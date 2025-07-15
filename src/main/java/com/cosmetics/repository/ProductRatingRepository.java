package com.cosmetics.repository;

import com.cosmetics.entity.Customer;
import com.cosmetics.entity.Product;
import com.cosmetics.entity.ProductRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRatingRepository extends JpaRepository<ProductRating, Integer> {

    List<ProductRating> findByProduct(Product product);
    
    Optional<ProductRating> findByProductAndCustomer(Product product, Customer customer);
    
    boolean existsByProductAndCustomer(Product product, Customer customer);
    
    Optional<ProductRating> findByCustomerAndProduct(Customer customer, Product product);
}
