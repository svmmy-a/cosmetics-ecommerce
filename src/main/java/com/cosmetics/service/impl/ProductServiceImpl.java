package com.cosmetics.service.impl;

import org.springframework.stereotype.Service;
import com.cosmetics.service.ProductService;
import com.cosmetics.repository.ProductRepository;
import com.cosmetics.mapper.DtoMapper;
import com.cosmetics.dto.ProductDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;
    private final DtoMapper mapper;

    public ProductServiceImpl(ProductRepository repo, DtoMapper mapper) {
        this.repo   = repo;
        this.mapper = mapper;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return repo.findAll()
                   .stream()
                   .map(mapper::toProductDto)
                   .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductById(Integer id) {
        return repo.findById(id)
                   .map(mapper::toProductDto)
                   .orElseThrow(() -> new IllegalArgumentException("Invalid product Id: " + id));
    }

    @Override
    public List<ProductDto> getNewProducts() {
        return repo.findAll()
                   .stream()
                   .filter(product -> product.getIsNew() != null && product.getIsNew())
                   .map(mapper::toProductDto)
                   .collect(Collectors.toList());
    }

    @Override
    public List<ProductDto> searchProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllProducts();
        }
        String term = searchTerm.toLowerCase();
        return repo.findAll()
                   .stream()
                   .filter(product -> 
                       (product.getName() != null && product.getName().toLowerCase().contains(term)) || 
                       (product.getDescription() != null && product.getDescription().toLowerCase().contains(term))
                   )
                   .map(mapper::toProductDto)
                   .collect(Collectors.toList());
    }

    @Override
    public com.cosmetics.entity.Product findById(Integer id) {
        return repo.findById(id)
                   .orElseThrow(() -> new IllegalArgumentException("Invalid product Id: " + id));
    }
    
    @Override
    public List<com.cosmetics.entity.Product> findAllProducts() {
        return repo.findAll();
    }
    
    @Override
    public com.cosmetics.entity.Product createProduct(String name, String description, 
                                                    java.math.BigDecimal price, 
                                                    Integer categoryId, String size, 
                                                    Boolean featured, Boolean isNew, 
                                                    org.springframework.web.multipart.MultipartFile imageFile) {
        // implmentation will be added later
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
/*************  ✨ Windsurf Command ⭐  *************/
/**
 * Updates an existing product with the given details.
 *
 * @param productId The ID of the product to update.
 * @param name The new name of the product.
 * @param description The new description of the product.
 * @param price The new price of the product.
 * @param categoryId The ID of the new category for the product.
 * @param size The new size of the product.
 * @param featured Whether the product is featured.
 * @param isNew Whether the product is new.
 * @param imageFile The new image file for the product, if any.
 * @return The updated product entity.
 * @throws IllegalArgumentException if the product ID is invalid.
 * @throws UnsupportedOperationException if the method is not yet implemented.
 */

/*******  ee327709-e1cd-493f-9848-03953985909d  *******/
    @Override
    public com.cosmetics.entity.Product updateProduct(Integer productId, String name, 
                                                    String description, java.math.BigDecimal price,
                                                    Integer categoryId, String size, 
                                                    Boolean featured, Boolean isNew,
                                                    org.springframework.web.multipart.MultipartFile imageFile) {
        // implementation will be added later
        throw new UnsupportedOperationException("Method not yet implemented");
    }
    
    @Override
    public com.cosmetics.entity.Product saveProduct(com.cosmetics.entity.Product product) {
        return repo.save(product);
    }
    
    @Override
    public void deleteProduct(Integer productId) {
        repo.deleteById(productId);
    }
    
    @Override
    public boolean isInStock(Integer productId) {
        // implementatoin will be added later
        return true; // temp implementation
    }
    
    @Override
    public int getStockQuantity(Integer productId) {
        // implementation will be added later
        return 10; // temp implementation
    }
}
