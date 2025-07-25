package com.cosmetics.service;

import java.util.List;

import com.cosmetics.entity.Warehouse;

/**
 * Service interface for handling warehouse related operations
 */
public interface WarehouseService {
    // read operations
    List<Warehouse> getAllWarehouses();
    List<Warehouse> findAll(); // alias for getAllWarehouses
    Warehouse findById(Integer id);

    // write operations
    Warehouse createWarehouse(String name, String location, String contactInfo);
    Warehouse updateWarehouse(Integer id, String name, String location, String contactInfo);
    // note: contactInfo is currently not used as the entity only has name and address fields
    void deleteWarehouse(Integer id);
}
