package com.cosmetics.controller;

import com.cosmetics.dto.ProductDto;
import com.cosmetics.service.ProductRatingService;
import com.cosmetics.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductWebController {

    private final ProductService productService;
    private final ProductRatingService productRatingService;

    @Autowired
    public ProductWebController(ProductService productService, ProductRatingService productRatingService) {
        this.productService = productService;
        this.productRatingService = productRatingService;
    }

    /**
     * renders the product list page, showing all or new products based on query.
     * @param isNew flag to filter new products if true
     * @param model model to pass data to the view
     * @return template name for product list page
     */
    @GetMapping("/products")
    public String listProducts(@RequestParam(value = "new", required = false) Boolean isNew, Model model) {
        List<ProductDto> products = (isNew != null && isNew) ? productService.getNewProducts() : productService.getAllProducts();
        // fetch avg rating and review count for each product
        Map<Integer, Double> ratings = new HashMap<>();
        Map<Integer, Integer> reviewCounts = new HashMap<>();
        for (ProductDto productDto : products) {
            com.cosmetics.entity.Product productEntity = productService.findById(productDto.getProductId());
            if (productEntity != null) {
                double avgRating = productRatingService.getAverageRating(productEntity);
                int reviewCount = productRatingService.getReviewsByProduct(productEntity).size();
                ratings.put(productDto.getProductId(), avgRating);
                reviewCounts.put(productDto.getProductId(), reviewCount);
            } else {
                ratings.put(productDto.getProductId(), 0.0);
                reviewCounts.put(productDto.getProductId(), 0);
            }
        }
        model.addAttribute("products", products);
        model.addAttribute("ratings", ratings);
        model.addAttribute("reviewCounts", reviewCounts);
        return "products";  // Thymeleaf template: src/main/resources/templates/products.html
    }
    
    /**
     * renders the product detail page for a specific product.
     * @param id the id of the product to display
     * @param model model to pass data to the view
     * @return template name for product detail page
     */
    @GetMapping("/product/{id}")
    public String showProductDetail(@PathVariable("id") Integer id, Model model) {
        ProductDto product = productService.getProductById(id);
        model.addAttribute("product", product);
        // fetch reviews and average rating for the product
        com.cosmetics.entity.Product productEntity = productService.findById(id);
        if (productEntity != null) {
            model.addAttribute("reviews", productRatingService.getReviewsByProduct(productEntity));
            model.addAttribute("averageRating", productRatingService.getAverageRating(productEntity));
            model.addAttribute("reviewCount", productRatingService.getReviewsByProduct(productEntity).size());
        } else {
            model.addAttribute("reviews", java.util.Collections.emptyList());
            model.addAttribute("averageRating", 0.0);
            model.addAttribute("reviewCount", 0);
        }
        return "product-detail";  // Thymeleaf template: src/main/resources/templates/product-detail.html
    }
}
