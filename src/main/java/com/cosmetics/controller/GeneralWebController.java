package com.cosmetics.controller;

import com.cosmetics.entity.Product;
import com.cosmetics.service.OrderService;
import com.cosmetics.service.ProductRatingService;
import com.cosmetics.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class GeneralWebController {

    private final OrderService orderService;
    private final ProductService productService;
    private final ProductRatingService productRatingService;

    @Autowired
    public GeneralWebController(OrderService orderService, ProductService productService, ProductRatingService productRatingService) {
        this.orderService = orderService;
        this.productService = productService;
        this.productRatingService = productRatingService;
    }

    /**
     * renders the index page as the application homepage.
     * @param model model to pass data to the view
     * @return template name for index page
     */
    @GetMapping({"/", "/index"})
    public String showIndex(Model model) {
        List<Map<String, Object>> topProducts = orderService.getTopSellingProducts();
        Map<Integer, Double> ratings = new HashMap<>();
        Map<Integer, Integer> reviewCounts = new HashMap<>();

        for (Map<String, Object> productData : topProducts) {
            Integer productId = (Integer) productData.get("productId");
            Product productEntity = productService.findById(productId);
            if (productEntity != null) {
                double avgRating = productRatingService.getAverageRating(productEntity);
                int reviewCount = productRatingService.getReviewsByProduct(productEntity).size();
                ratings.put(productId, avgRating);
                reviewCounts.put(productId, reviewCount);
            } else {
                ratings.put(productId, 0.0);
                reviewCounts.put(productId, 0);
            }
        }

        model.addAttribute("topProducts", topProducts);
        model.addAttribute("ratings", ratings);
        model.addAttribute("reviewCounts", reviewCounts);
        return "index";  // Thymeleaf template: src/main/resources/templates/index.html
    }
    
    /**
     * renders the wishlist page for saved user items.
     * @param model model to pass data to the view
     * @return template name for wishlist page
     */
    @GetMapping("/wishlist")
    public String showWishlist(Model model) {
        return "wishlist";  // Thymeleaf template: src/main/resources/templates/wishlist.html
    }
}
