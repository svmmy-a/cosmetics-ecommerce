package com.cosmetics.dto;

/**
 * DTO for product data including rating information.
 */
public class ProductWithRatingDto {

    private ProductDto product;
    private double averageRating;
    private int reviewCount;

    public ProductWithRatingDto() {
    }

    public ProductWithRatingDto(ProductDto product, double averageRating, int reviewCount) {
        this.product = product;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}
