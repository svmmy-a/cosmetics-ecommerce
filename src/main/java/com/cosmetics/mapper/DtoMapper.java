package com.cosmetics.mapper;

import org.springframework.stereotype.Component;
import com.cosmetics.entity.*;
import com.cosmetics.dto.CategoryDto;
import com.cosmetics.dto.InventoryDto;
import com.cosmetics.dto.ProductDto;
import com.cosmetics.dto.ProductSupplierDto;
import com.cosmetics.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DtoMapper {

    private final InventoryRepository inventoryRepository;

    @Autowired
    public DtoMapper(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public CategoryDto toCategoryDto(Category c) {
        if (c == null) return null;
        return new CategoryDto(c.getCategoryId(), c.getName());
    }

    public ProductDto toProductDto(Product p) {
        if (p == null) return null;
        Integer totalStock = inventoryRepository.findByProduct(p)
                .stream()
                .mapToInt(Inventory::getQuantity)
                .sum();
        return new ProductDto(
            p.getProductId(),
            p.getName(),
            p.getDescription(),
            p.getPrice().doubleValue(),
            p.getSize(),
            p.getImageUrl(),
            p.getIsNew(),
            totalStock,
            toCategoryDto(p.getCategory())
        );
    }

    public InventoryDto toInventoryDto(Inventory i) {
        if (i == null) return null;
        return new InventoryDto(
            i.getInventoryId(),
            i.getProduct() != null ? i.getProduct().getProductId() : null,
            i.getProduct() != null ? i.getProduct().getName() : null,
            i.getProduct() != null ? i.getProduct().getPrice().doubleValue() : 0.0,
            i.getWarehouse() != null ? i.getWarehouse().getWarehouseId() : null,
            i.getWarehouse() != null ? i.getWarehouse().getName() : null,
            i.getQuantity(),
            i.getBuyPrice(),
            i.getSupplier() != null ? i.getSupplier().getSupplierId() : null,
            i.getSupplier() != null ? i.getSupplier().getName() : null,
            i.getSupplier() != null ? i.getSupplier().getEmail() : null,
            i.getSupplier() != null ? i.getSupplier().getPhone() : null,
            i.getSupplier() != null ? i.getSupplier().getAddress() : null,
            i.getLastUpdated()
        );
    }
    
    public ProductSupplierDto toProductSupplierDto(ProductSupplier ps) {
        if (ps == null) return null;
        ProductSupplierDto dto = new ProductSupplierDto();
        dto.setProductSupplierId(ps.getProductSupplierId());
        dto.setProductId(ps.getProduct() != null ? ps.getProduct().getProductId() : null);
        dto.setProductName(ps.getProduct() != null ? ps.getProduct().getName() : null);
        dto.setSupplierId(ps.getSupplier() != null ? ps.getSupplier().getSupplierId() : null);
        dto.setSupplierName(ps.getSupplier() != null ? ps.getSupplier().getName() : null);
        dto.setQuantity(ps.getQuantity());
        dto.setBuyPrice(ps.getBuyPrice());
        dto.setSupplyDate(ps.getSupplyDate());
        return dto;
    }
}
