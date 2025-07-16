package com.cosmetics.service;

import com.cosmetics.dto.InventoryDto;

import java.util.List;

public interface InventoryService {
    List<InventoryDto> getAllInventory();
    InventoryDto getInventoryById(Integer id);
    InventoryDto updateInventoryQuantity(Integer id, Integer quantity);
    InventoryDto addInventory(Integer productId, Integer warehouseId, Integer quantity, Double buyPrice, Integer supplierId);
    InventoryDto deleteInventory(Integer id);
    List<InventoryDto> getLowStockProducts();
}
