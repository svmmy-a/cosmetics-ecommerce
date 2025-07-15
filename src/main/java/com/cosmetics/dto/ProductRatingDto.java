package com.cosmetics.dto;

import java.time.LocalDateTime;

public class ProductRatingDto {

    private Integer ratingId;
    private Integer productId;
    private String customerName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    // constructor
    public ProductRatingDto() {
    }

    public ProductRatingDto(Integer ratingId, Integer productId, String customerName, Integer rating, String comment, LocalDateTime createdAt) {
        this.ratingId = ratingId;
        this.productId = productId;
        this.customerName = customerName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // get and setters
    public Integer getRatingId() {
        return ratingId;
    }

    public void setRatingId(Integer ratingId) {
        this.ratingId = ratingId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
