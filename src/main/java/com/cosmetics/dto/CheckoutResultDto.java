package com.cosmetics.dto;

import com.cosmetics.entity.Address;

import java.util.List;

/**
 * DTO for checkout and payment results.
 */
public class CheckoutResultDto {
    private List<CartItemDto> items;
    private List<Address> savedAddresses;
    private double subtotal;
    private double deliveryCost;
    private double total;
    private String error;

    public CheckoutResultDto() {
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }

    public List<Address> getSavedAddresses() {
        return savedAddresses;
    }

    public void setSavedAddresses(List<Address> savedAddresses) {
        this.savedAddresses = savedAddresses;
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
