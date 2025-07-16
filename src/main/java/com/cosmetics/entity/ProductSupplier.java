package com.cosmetics.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_supplier")
public class ProductSupplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_supplier_id")
    private Integer productSupplierId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "buy_price", nullable = false)
    private Double buyPrice;

    @Column(name = "supply_date")
    private LocalDateTime supplyDate;

    // Constructor
    public ProductSupplier() {
    }

    public ProductSupplier(Product product, Supplier supplier, Integer quantity, Double buyPrice, LocalDateTime supplyDate) {
        this.product = product;
        this.supplier = supplier;
        this.quantity = quantity;
        this.buyPrice = buyPrice;
        this.supplyDate = supplyDate;
    }

    // Getters and Setters
    public Integer getProductSupplierId() {
        return productSupplierId;
    }

    public void setProductSupplierId(Integer productSupplierId) {
        this.productSupplierId = productSupplierId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
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

    public LocalDateTime getSupplyDate() {
        return supplyDate;
    }

    public void setSupplyDate(LocalDateTime supplyDate) {
        this.supplyDate = supplyDate;
    }
}
