package com.cosmetics.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.cosmetics.dto.ProductSupplierDto;
import com.cosmetics.entity.Product;
import com.cosmetics.entity.ProductSupplier;
import com.cosmetics.entity.Supplier;
import com.cosmetics.entity.Warehouse;

/**
 * Service interface for handling product supplier transactions
 */
public interface ProductSupplierService {
    // read operations
    List<ProductSupplier> getAllTransactions();
    List<ProductSupplier> getTransactionsByProduct(Integer productId);
    ProductSupplier findById(Integer id);
    
    // write operations
    ProductSupplier createTransaction(Product product, Supplier supplier, 
                                    Warehouse warehouse, Integer quantity, 
                                    BigDecimal cost, Date transactionDate);
    
    ProductSupplier updateTransaction(Integer id, Product product, Supplier supplier, 
                                    Warehouse warehouse, Integer quantity, 
                                    BigDecimal cost, Date transactionDate);
    
    void deleteTransaction(Integer id);
    
    // conversion methods
    ProductSupplierDto convertToDto(ProductSupplier transaction);
    List<ProductSupplierDto> convertToDtoList(List<ProductSupplier> transactions);
}
