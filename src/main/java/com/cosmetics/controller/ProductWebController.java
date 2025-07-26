package com.cosmetics.controller;

import com.cosmetics.dto.ProductWithRatingDto;
import com.cosmetics.entity.Product;
import com.cosmetics.service.ProductDisplayService;
import com.cosmetics.service.ProductRatingService;
import com.cosmetics.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductWebController {

    private final ProductDisplayService productDisplayService;
    private final ProductRatingService productRatingService;
    private final ProductService productService;

    @Autowired
    public ProductWebController(ProductDisplayService productDisplayService, 
                              ProductRatingService productRatingService,
                              ProductService productService) {
        this.productDisplayService = productDisplayService;
        this.productRatingService = productRatingService;
        this.productService = productService;
    }

    /**
     * renders the product list page, showing all or new products based on query.
     * @param isNew flag to filter new products if true
     * @param model model to pass data to the view
     * @return template name for product list page
     */
    @GetMapping("/products")
    public String listProducts(@RequestParam(value = "new", required = false) Boolean isNew, Model model) {
        List<ProductWithRatingDto> productsWithRatings = productDisplayService.getProductsWithRatings(isNew);
        
        // extract products for the template
        model.addAttribute("products", productsWithRatings.stream().map(p -> p.getProduct()).toList());
        
        // create maps for ratings and review counts for backward compatibility with view
        Map<Integer, Double> ratings = new HashMap<>();
        Map<Integer, Integer> reviewCounts = new HashMap<>();
        
        // populate the maps from the productsWithRatings list
        for (ProductWithRatingDto productDto : productsWithRatings) {
            ratings.put(productDto.getProduct().getProductId(), productDto.getAverageRating());
            reviewCounts.put(productDto.getProduct().getProductId(), productDto.getReviewCount());
        }
        
        // add the maps to the model
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
        ProductWithRatingDto productWithRating = productDisplayService.getProductWithDetailedRatings(id);
        
        model.addAttribute("product", productWithRating.getProduct());
        model.addAttribute("averageRating", productWithRating.getAverageRating());
        model.addAttribute("reviewCount", productWithRating.getReviewCount());
        
        // get the product entity to fetch reviews
        Product productEntity = null;
        try {
            productEntity = productService.findById(id);
        } catch (Exception e) {
            // handle exception
        }
        
        if (productEntity != null) {
            model.addAttribute("reviews", productRatingService.getReviewsByProduct(productEntity));
        } else {
            model.addAttribute("reviews", Collections.emptyList());
        }
        
        return "product-detail";  // Thymeleaf template: src/main/resources/templates/product-detail.html
    }
}
