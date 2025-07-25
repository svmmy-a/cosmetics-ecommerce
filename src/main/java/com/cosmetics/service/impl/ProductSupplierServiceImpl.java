package com.cosmetics.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmetics.dto.ProductSupplierDto;
import com.cosmetics.entity.Product;
import com.cosmetics.entity.ProductSupplier;
import com.cosmetics.entity.Supplier;
import com.cosmetics.entity.Warehouse;
import com.cosmetics.mapper.DtoMapper;
import com.cosmetics.repository.ProductSupplierRepository;
import com.cosmetics.service.ProductSupplierService;

@Service
@Transactional(readOnly = true)
public class ProductSupplierServiceImpl implements ProductSupplierService {

    private final ProductSupplierRepository productSupplierRepository;
    private final DtoMapper dtoMapper;
    
    @Autowired
    public ProductSupplierServiceImpl(ProductSupplierRepository productSupplierRepository, DtoMapper dtoMapper) {
        this.productSupplierRepository = productSupplierRepository;
        this.dtoMapper = dtoMapper;
    }
    
    @Override
    public List<ProductSupplier> getAllTransactions() {
        return productSupplierRepository.findAll();
    }

    @Override
    public List<ProductSupplier> getTransactionsByProduct(Integer productId) {
        return productSupplierRepository.findByProduct_ProductId(productId);
    }

    @Override
    public ProductSupplier findById(Integer id) {
        return productSupplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction ID: " + id));
    }

    @Override
    @Transactional
    public ProductSupplier createTransaction(Product product, Supplier supplier, 
                                          Warehouse warehouse, Integer quantity, 
                                          BigDecimal cost, Date transactionDate) {
        ProductSupplier transaction = new ProductSupplier();
        transaction.setProduct(product);
        transaction.setSupplier(supplier);
        // ProductSupplier does not have a warehouse field
        transaction.setQuantity(quantity);
        transaction.setBuyPrice(cost.doubleValue()); // using buyPrice instead of cost
        
        // Convert java.util.Date to LocalDateTime
        LocalDateTime supplyDate = transactionDate != null 
            ? new java.sql.Timestamp(transactionDate.getTime()).toLocalDateTime() 
            : LocalDateTime.now();
        transaction.setSupplyDate(supplyDate); // Using supplyDate instead of transactionDate
        
        return productSupplierRepository.save(transaction);
    }

    @Override
    @Transactional
    public ProductSupplier updateTransaction(Integer id, Product product, Supplier supplier, 
                                          Warehouse warehouse, Integer quantity, 
                                          BigDecimal cost, Date transactionDate) {
        ProductSupplier transaction = findById(id);
        transaction.setProduct(product);
        transaction.setSupplier(supplier);
        // ProductSupplier does not have a warehouse field
        transaction.setQuantity(quantity);
        transaction.setBuyPrice(cost.doubleValue()); // using buyPrice instead of cost
        
        // covert java.util.Date to LocalDateTime
        if (transactionDate != null) {
            LocalDateTime supplyDate = new java.sql.Timestamp(transactionDate.getTime()).toLocalDateTime();
            transaction.setSupplyDate(supplyDate); // using supplyDate instead of transactionDate
        }
        
        return productSupplierRepository.save(transaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Integer id) {
        ProductSupplier transaction = findById(id);
        productSupplierRepository.delete(transaction);
    }

    @Override
    public ProductSupplierDto convertToDto(ProductSupplier transaction) {
        return dtoMapper.toProductSupplierDto(transaction);
    }

    @Override
    public List<ProductSupplierDto> convertToDtoList(List<ProductSupplier> transactions) {
        return transactions.stream()
                .map(dtoMapper::toProductSupplierDto)
                .collect(Collectors.toList());
    }
}
