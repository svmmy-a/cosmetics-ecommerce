package com.cosmetics.service;

import com.cosmetics.dto.ProductDto;
import com.cosmetics.dto.ProductWithRatingDto;
import com.cosmetics.entity.Product;

import java.util.List;
import java.util.Map;

/**
 * Service interface for handling product display operations.
 * Separates display-specific product operations from core product management.
 */
public interface ProductDisplayService {
    
    /**
     * Get all products with their ratings and review counts.
     * 
     * @param isNew optional filter for new products only
     * @return list of products with ratings
     */
    List<ProductWithRatingDto> getProductsWithRatings(Boolean isNew);
    
    /**
     * Get a specific product with detailed rating information.
     * 
     * @param productId the id of the product
     * @return product with detailed rating information
     */
    ProductWithRatingDto getProductWithDetailedRatings(Integer productId);
}
