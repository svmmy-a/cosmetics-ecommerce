// src/main/java/com/cosmetics/repository/CategoryRepository.java
package com.cosmetics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cosmetics.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> { }