package com.cosmetics.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmetics.entity.Warehouse;
import com.cosmetics.repository.WarehouseRepository;
import com.cosmetics.service.WarehouseService;

@Service
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    
    @Autowired
    public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }
    
    @Override
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }
    
    @Override
    public List<Warehouse> findAll() {
        return getAllWarehouses();
    }

    @Override
    public Warehouse findById(Integer id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid warehouse ID: " + id));
    }

    @Override
    @Transactional
    public Warehouse createWarehouse(String name, String location, String contactInfo) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(name);
        warehouse.setAddress(location); 
        return warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional
    public Warehouse updateWarehouse(Integer id, String name, String location, String contactInfo) {
        Warehouse warehouse = findById(id);
        warehouse.setName(name);
        warehouse.setAddress(location); 
        return warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional
    public void deleteWarehouse(Integer id) {
        Warehouse warehouse = findById(id);
        warehouseRepository.delete(warehouse);
    }
}
