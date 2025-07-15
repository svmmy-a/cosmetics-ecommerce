package com.cosmetics.controller;

import com.cosmetics.dto.ProductDto;
import com.cosmetics.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Integer id) {
        ProductDto product = productService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String term) {
        List<ProductDto> products = productService.searchProducts(term);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/new")
    public ResponseEntity<List<ProductDto>> getNewProducts() {
        List<ProductDto> products = productService.getNewProducts();
        return ResponseEntity.ok(products);
    }
}
