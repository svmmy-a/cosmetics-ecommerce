package com.cosmetics.service.impl;

import com.cosmetics.dto.InventoryDto;
import com.cosmetics.entity.Inventory;
import com.cosmetics.entity.Product;
import com.cosmetics.entity.ProductSupplier;
import com.cosmetics.entity.Supplier;
import com.cosmetics.entity.Warehouse;
import com.cosmetics.mapper.DtoMapper;
import com.cosmetics.repository.InventoryRepository;
import com.cosmetics.repository.ProductRepository;
import com.cosmetics.repository.ProductSupplierRepository;
import com.cosmetics.repository.SupplierRepository;
import com.cosmetics.repository.WarehouseRepository;
import com.cosmetics.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private DtoMapper dtoMapper;

    @Override
    public List<InventoryDto> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(dtoMapper::toInventoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryDto> getLowStockProducts() {
        int LOW_STOCK_THRESHOLD = 10;
        return inventoryRepository.findAll().stream()
                .filter(inventory -> inventory.getQuantity() < LOW_STOCK_THRESHOLD)
                .map(dtoMapper::toInventoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDto getInventoryById(Integer id) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(id);
        return inventoryOpt.map(dtoMapper::toInventoryDto).orElse(null);
    }

    @Override
    public InventoryDto updateInventoryQuantity(Integer id, Integer quantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(id);
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            inventory.setQuantity(quantity);
            inventory.setLastUpdated(LocalDateTime.now());
            inventoryRepository.save(inventory);
            return dtoMapper.toInventoryDto(inventory);
        }
        return null;
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductSupplierRepository productSupplierRepository;

    @Override
    public InventoryDto addInventory(Integer productId, Integer warehouseId, Integer quantity, Double buyPrice, Integer supplierId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(warehouseId);
        Supplier supplier = null;
        if (supplierId != null) {
            Optional<Supplier> supplierOpt = supplierRepository.findById(supplierId);
            supplier = supplierOpt.orElse(null);
        }

        if (productOpt.isPresent() && warehouseOpt.isPresent()) {
            Product product = productOpt.get();
            Warehouse warehouse = warehouseOpt.get();
            
            // check for existing inventory record
            Optional<Inventory> existingInventoryOpt = inventoryRepository.findByProductAndWarehouse(product, warehouse);
            Inventory inventory;
            if (existingInventoryOpt.isPresent()) {
                inventory = existingInventoryOpt.get();
                inventory.setQuantity(inventory.getQuantity() + quantity);
                inventory.setBuyPrice(buyPrice);
                inventory.setLastUpdated(LocalDateTime.now());
            } else {
                inventory = new Inventory(product, warehouse, quantity, buyPrice, supplier);
                inventory.setLastUpdated(LocalDateTime.now());
            }
            inventoryRepository.save(inventory);

            // log the transaction in ProductSupplier for historical tracking
            ProductSupplier productSupplier = new ProductSupplier(product, supplier, quantity, buyPrice, LocalDateTime.now());
            productSupplierRepository.save(productSupplier);

            return dtoMapper.toInventoryDto(inventory);
        }
        return null;
    }

    @Override
    public InventoryDto deleteInventory(Integer id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
        inventoryRepository.delete(inventory);
        return dtoMapper.toInventoryDto(inventory);
    }
}
