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
}
