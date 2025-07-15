package com.cosmetics.service;

import com.cosmetics.dto.ProductRatingDto;
import com.cosmetics.entity.Customer;
import com.cosmetics.entity.Product;
import com.cosmetics.entity.ProductRating;

import java.util.List;

public interface ProductRatingService {

    /**
     * Submits a review for a product by a customer, after validating purchase history and uniqueness.
     *
     * @param customer the customer submitting the review
     * @param product the product being reviewed
     * @param rating the rating value (1-5)
     * @param comment the review comment
     * @return the saved ProductRating entity
     * @throws IllegalStateException if the customer hasnt purchased the product or has already reviewed it
     * @throws IllegalArgumentException if the rating is out of range
     */
    ProductRating submitReview(Customer customer, Product product, int rating, String comment);

    /**
     * Retrieves all reviews for a specific product.
     *
     * @param product the product to fetch reviews for
     * @return a list of ProductRatingDto objects representing the reviews
     */
    List<ProductRatingDto> getReviewsByProduct(Product product);

    /**
     * Calculates the average rating for a specific product.
     *
     * @param product the product to calculate the average rating for
     * @return the average rating as a double, or 0.0 if no reviews exist
     */
    double getAverageRating(Product product);

    /**
     * Checks if a customer has already reviewed a specific product.
     *
     * @param customer the customer to check
     * @param product the product to check
     * @return true if the customer has reviewed the product, false otherwise
     */
    boolean hasReviewed(Customer customer, Product product);

    /**
     * Checks if a customer has purchased a specific product (in a delivered order).
     *
     * @param customer the customer to check
     * @param product the product to check
     * @return true if the customer has purchased the product, false otherwise
     */
    boolean hasPurchased(Customer customer, Product product);
    
    /**
     * Finds a specific review by a customer for a product.
     *
     * @param customer the customer who made the review
     * @param product the product reviewed
     * @return the ProductRating entity if found, null otherwise
     */
    ProductRating findByCustomerAndProduct(Customer customer, Product product);
}
