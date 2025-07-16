package com.cosmetics.dto;

import java.time.LocalDateTime;

public class InventoryDto {

    private Integer inventoryId;
    private Integer productId;
    private String productName;
    private Double price;
    private Integer warehouseId;
    private String warehouseName;
    private Integer quantity;
    private Double buyPrice;
    private Integer supplierId;
    private String supplierName;
    private String supplierEmail;
    private String supplierPhone;
    private String supplierAddress;
    private LocalDateTime lastUpdated;

    public InventoryDto(Integer inventoryId, Integer productId, String productName, Double price, Integer warehouseId, String warehouseName, Integer quantity, Double buyPrice, Integer supplierId, String supplierName, String supplierEmail, String supplierPhone, String supplierAddress, LocalDateTime lastUpdated) {
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.supplierEmail = supplierEmail;
        this.supplierPhone = supplierPhone;
        this.supplierAddress = supplierAddress;
        this.lastUpdated = lastUpdated;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(Double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public void setSupplierEmail(String supplierEmail) {
        this.supplierEmail = supplierEmail;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }

    public void setSupplierPhone(String supplierPhone) {
        this.supplierPhone = supplierPhone;
    }

    public String getSupplierAddress() {
        return supplierAddress;
    }

    public void setSupplierAddress(String supplierAddress) {
        this.supplierAddress = supplierAddress;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
