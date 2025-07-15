package com.cosmetics.service.impl;

import com.cosmetics.dto.ProductRatingDto;
import com.cosmetics.entity.Customer;
import com.cosmetics.entity.Order;
import com.cosmetics.entity.Product;
import com.cosmetics.entity.ProductRating;
import com.cosmetics.repository.ProductRatingRepository;
import com.cosmetics.service.OrderService;
import com.cosmetics.service.ProductRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductRatingServiceImpl implements ProductRatingService {

    @Autowired
    private ProductRatingRepository productRatingRepository;

    @Autowired
    private OrderService orderService;

    @Override
    public ProductRating submitReview(Customer customer, Product product, int rating, String comment) {
        // validate rating range as a safeguard, even though UI dropdown restricts input to 1-5
        // protection against manipulated requests or future UI changes
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        if (!hasPurchased(customer, product)) {
            throw new IllegalStateException("You must purchase this product before leaving a review");
        }

        ProductRating review = productRatingRepository.findByCustomerAndProduct(customer, product).orElse(new ProductRating(product, customer, rating, comment));
        review.setRating(rating);
        review.setComment(comment);
        return productRatingRepository.save(review);
    }

    @Override
    public List<ProductRatingDto> getReviewsByProduct(Product product) {
        List<ProductRating> ratings = productRatingRepository.findByProduct(product);
        return ratings.stream()
                .map(rating -> new ProductRatingDto(
                        rating.getRatingId(),
                        rating.getProduct().getProductId(),
                        rating.getCustomer().getFullName(),
                        rating.getRating(),
                        rating.getComment(),
                        rating.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public double getAverageRating(Product product) {
        List<ProductRating> ratings = productRatingRepository.findByProduct(product);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToInt(ProductRating::getRating)
                .average()
                .orElse(0.0);
    }

    @Override
    public boolean hasReviewed(Customer customer, Product product) {
        return productRatingRepository.findByCustomerAndProduct(customer, product).isPresent();
    }

    @Override
    public boolean hasPurchased(Customer customer, Product product) {
        List<Order> orders = orderService.findByCustomer(customer);
        return orders.stream()
                .filter(order -> order.getOrderStatus() == Order.OrderStatus.delivered)
                .flatMap(order -> order.getOrderItems().stream())
                .anyMatch(orderItem -> orderItem.getProduct().getProductId().equals(product.getProductId()));
    }

    @Override
    public ProductRating findByCustomerAndProduct(Customer customer, Product product) {
        return productRatingRepository.findByCustomerAndProduct(customer, product).orElse(null);
    }
}
