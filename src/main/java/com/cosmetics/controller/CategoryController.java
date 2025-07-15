// src/main/java/com/cosmetics/controller/CategoryController.java
package com.cosmetics.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import com.cosmetics.dto.CategoryDto;
import com.cosmetics.entity.Category;
import com.cosmetics.repository.CategoryRepository;
import com.cosmetics.mapper.DtoMapper;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepo;
    private final DtoMapper mapper;

    public CategoryController(CategoryRepository categoryRepo, DtoMapper mapper) {
        this.categoryRepo = categoryRepo;
        this.mapper       = mapper;
    }

    /** GET /api/categories */
    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryRepo.findAll()
                           .stream()
                           .map(mapper::toCategoryDto)
                           .collect(Collectors.toList());
    }

    /** GET /api/categories/{id} */
    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Integer id) {
        Category cat = categoryRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        return mapper.toCategoryDto(cat);
    }
}