package com.cosmetics.service;

import java.util.List;

import com.cosmetics.entity.Category;

/**
 * Service interface for handling category related operations
 */
public interface CategoryService {
    // read operations
    List<Category> getAllCategories();
    Category findById(Integer id);
    java.util.Optional<Category> findCategoryById(Integer id);
    
    // write operations
    Category createCategory(String name, String description);
    Category updateCategory(Integer id, String name, String description);
    // note: description parameter is kept for future enhancements but not currently used
    void deleteCategory(Integer id);
}
