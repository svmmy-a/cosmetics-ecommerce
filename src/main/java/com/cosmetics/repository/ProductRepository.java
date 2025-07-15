// src/main/java/com/cosmetics/repository/ProductRepository.java
package com.cosmetics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cosmetics.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> { }