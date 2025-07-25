package com.cosmetics.service;

import java.util.List;

import com.cosmetics.entity.Supplier;

/**
 * Service interface for handling supplier related operations
 */
public interface SupplierService {
    // Read operations
    List<Supplier> getAllSuppliers();
    List<Supplier> findAll(); // alias for getAllSuppliers
    Supplier findById(Integer id);
    
    // write operations
    Supplier createSupplier(String name, String contactInfo, String address);
    Supplier updateSupplier(Integer id, String name, String contactInfo, String address);
    void deleteSupplier(Integer id);
}
