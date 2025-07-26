package com.cosmetics.dto;

import java.util.List;

/**
 * DTO for cart summary information.
 */
public class CartSummaryDto {
    private List<CartItemDto> items;
    private double subtotal;
    private double deliveryCost;
    private double total;

    public CartSummaryDto() {
    }

    public CartSummaryDto(List<CartItemDto> items, double subtotal, double deliveryCost, double total) {
        this.items = items;
        this.subtotal = subtotal;
        this.deliveryCost = deliveryCost;
        this.total = total;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
