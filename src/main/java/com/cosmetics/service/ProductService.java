package com.cosmetics.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import com.cosmetics.dto.ProductDto;
import com.cosmetics.entity.Product;

/**
 * Service interface for handling product-related operations.
 */
public interface ProductService {
    // Read operations
    List<ProductDto> getAllProducts();
    ProductDto getProductById(Integer id);
    Product findById(Integer id);
    List<ProductDto> getNewProducts();
    List<ProductDto> searchProducts(String searchTerm);
    List<Product> findAllProducts();
    
    // Write operations
    Product createProduct(String name, String description, BigDecimal price, 
                        Integer categoryId, String size, Boolean featured, 
                        Boolean isNew, MultipartFile imageFile);
    
    Product updateProduct(Integer productId, String name, String description, BigDecimal price,
                        Integer categoryId, String size, Boolean featured, Boolean isNew,
                        MultipartFile imageFile);
    
    Product saveProduct(Product product);
    
    void deleteProduct(Integer productId);
    
    // Stock management
    boolean isInStock(Integer productId);
    int getStockQuantity(Integer productId);
}
