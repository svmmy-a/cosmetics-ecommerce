// src/main/java/com/cosmetics/service/ProductService.java
package com.cosmetics.service;

import java.util.List;
import com.cosmetics.dto.ProductDto;

public interface ProductService {
    List<ProductDto> getAllProducts();
    ProductDto getProductById(Integer id);
    List<ProductDto> getNewProducts();
    List<ProductDto> searchProducts(String searchTerm);
    com.cosmetics.entity.Product findById(Integer id);
}
