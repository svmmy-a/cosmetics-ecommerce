package com.cosmetics.dto;

import java.time.LocalDate;
public class CustomerDto {

    private Integer customerId;
    private String fullName;
    private String email;
    private long orderCount;
    private double totalSpend;
    private LocalDate lastOrderDate;

    // constructor
    public CustomerDto(Integer customerId, String fullName, String email, long orderCount, double totalSpend, LocalDate lastOrderDate) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.email = email;
        this.orderCount = orderCount;
        this.totalSpend = totalSpend;
        this.lastOrderDate = lastOrderDate;
    }

    // get and set
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(long orderCount) {
        this.orderCount = orderCount;
    }

    public double getTotalSpend() {
        return totalSpend;
    }

    public void setTotalSpend(double totalSpend) {
        this.totalSpend = totalSpend;
    }

    public LocalDate getLastOrderDate() {
        return lastOrderDate;
    }

    public void setLastOrderDate(LocalDate lastOrderDate) {
        this.lastOrderDate = lastOrderDate;
    }
}
