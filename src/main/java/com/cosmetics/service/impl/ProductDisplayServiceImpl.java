package com.cosmetics.service.impl;

import com.cosmetics.dto.ProductDto;
import com.cosmetics.dto.ProductWithRatingDto;
import com.cosmetics.entity.Product;
import com.cosmetics.service.ProductDisplayService;
import com.cosmetics.service.ProductRatingService;
import com.cosmetics.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ProductDisplayService for handling product display operations.
 */
@Service
public class ProductDisplayServiceImpl implements ProductDisplayService {

    private final ProductService productService;
    private final ProductRatingService productRatingService;

    @Autowired
    public ProductDisplayServiceImpl(ProductService productService, ProductRatingService productRatingService) {
        this.productService = productService;
        this.productRatingService = productRatingService;
    }

    @Override
    public List<ProductWithRatingDto> getProductsWithRatings(Boolean isNew) {
        // get products based on filter
        List<ProductDto> products = (isNew != null && isNew) 
                ? productService.getNewProducts() 
                : productService.getAllProducts();
        
        // transform to ProductWithRatingDto with ratings
        return products.stream()
                .map(productDto -> {
                    Product productEntity = productService.findById(productDto.getProductId());
                    double avgRating = 0.0;
                    int reviewCount = 0;
                    
                    if (productEntity != null) {
                        avgRating = productRatingService.getAverageRating(productEntity);
                        reviewCount = productRatingService.getReviewsByProduct(productEntity).size();
                    }
                    
                    return new ProductWithRatingDto(productDto, avgRating, reviewCount);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ProductWithRatingDto getProductWithDetailedRatings(Integer productId) {
        ProductDto productDto = productService.getProductById(productId);
        Product productEntity = productService.findById(productId);
        
        double avgRating = 0.0;
        int reviewCount = 0;
        
        if (productEntity != null) {
            avgRating = productRatingService.getAverageRating(productEntity);
            reviewCount = productRatingService.getReviewsByProduct(productEntity).size();
        }
        
        ProductWithRatingDto result = new ProductWithRatingDto(productDto, avgRating, reviewCount);
        return result;
    }
}
