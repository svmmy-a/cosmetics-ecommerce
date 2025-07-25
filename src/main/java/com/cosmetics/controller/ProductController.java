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
    
    @Autowired
    private com.cosmetics.service.ProductRatingService productRatingService;

    /**
     * Helper method to add average ratings and review counts to a list of products
     * @param products list of ProductDto objects to enhance with ratings and review counts
     */
    private void addAverageRatingsToProducts(List<ProductDto> products) {
        products.forEach(product -> {
            com.cosmetics.entity.Product productEntity = productService.findById(product.getProductId());
            if (productEntity != null) {
                double avgRating = productRatingService.getAverageRating(productEntity);
                int reviewCount = productRatingService.getReviewsByProduct(productEntity).size();
                product.setAverageRating(avgRating);
                product.setReviewCount(reviewCount);
            } else {
                product.setAverageRating(0.0);
                product.setReviewCount(0);
            }
        });
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.getAllProducts();
        addAverageRatingsToProducts(products);
        return ResponseEntity.ok(products);
    }

    /**
     * Helper method to add average rating and review count to a single product
     * @param product ProductDto to enhance with rating and review count
     * @param id product ID to find the product entity
     */
    private void addAverageRatingToProduct(ProductDto product, Integer id) {
        com.cosmetics.entity.Product productEntity = productService.findById(id);
        if (productEntity != null) {
            double avgRating = productRatingService.getAverageRating(productEntity);
            int reviewCount = productRatingService.getReviewsByProduct(productEntity).size();
            product.setAverageRating(avgRating);
            product.setReviewCount(reviewCount);
        } else {
            product.setAverageRating(0.0);
            product.setReviewCount(0);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Integer id) {
        ProductDto product = productService.getProductById(id);
        if (product != null) {
            addAverageRatingToProduct(product, id);
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String term) {
        List<ProductDto> products = productService.searchProducts(term);
        addAverageRatingsToProducts(products);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/new")
    public ResponseEntity<List<ProductDto>> getNewProducts() {
        List<ProductDto> products = productService.getNewProducts();
        addAverageRatingsToProducts(products);
        return ResponseEntity.ok(products);
    }
}
