package com.cosmetics.controller;

import com.cosmetics.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for analytics-related endpoints.
 * Provides data for dashboards and reports.
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/revenue/total")
    @ResponseBody
    public BigDecimal getTotalRevenue() {
        return analyticsService.calculateTotalRevenue();
    }

    @GetMapping("/orders/count")
    @ResponseBody
    public long getOrderCount() {
        return analyticsService.countTotalOrders();
    }

    @GetMapping("/orders/pending")
    @ResponseBody
    public long getPendingDeliveries() {
        return analyticsService.countPendingDeliveries();
    }

    @GetMapping("/revenue/growth")
    @ResponseBody
    public double getRevenueGrowth() {
        return analyticsService.calculateRevenueGrowthPercentage();
    }

    @GetMapping("/orders/growth")
    @ResponseBody
    public double getOrdersGrowth() {
        return analyticsService.calculateOrdersGrowthPercentage();
    }

    @GetMapping("/products/top-selling")
    @ResponseBody
    public List<Map<String, Object>> getTopSellingProducts(
            @RequestParam(required = false, defaultValue = "monthly") String range) {
        return analyticsService.getTopSellingProductsByRange(range);
    }

    @GetMapping("/products/sales")
    @ResponseBody
    public List<Map<String, Object>> getAllProductSalesData() {
        return analyticsService.getAllProductSalesData();
    }

    @GetMapping("/sales/data")
    @ResponseBody
    public List<Map<String, Object>> getSalesData(
            @RequestParam(required = false, defaultValue = "last28days") String range) {
        return analyticsService.getSalesDataForRange(range);
    }

    @GetMapping("/revenue/current-month")
    @ResponseBody
    public BigDecimal getCurrentMonthRevenue() {
        return analyticsService.calculateRevenueForCurrentMonth();
    }

    @GetMapping("/revenue/previous-month")
    @ResponseBody
    public BigDecimal getPreviousMonthRevenue() {
        return analyticsService.calculateRevenueForPreviousMonth();
    }

    @GetMapping("/orders/current-month")
    @ResponseBody
    public long getCurrentMonthOrders() {
        return analyticsService.countOrdersForCurrentMonth();
    }

    @GetMapping("/orders/previous-month")
    @ResponseBody
    public long getPreviousMonthOrders() {
        return analyticsService.countOrdersForPreviousMonth();
    }

    @GetMapping("/customer/order-count")
    @ResponseBody
    public long getOrderCountByCustomer(@RequestParam Integer customerId) {
        return analyticsService.getOrderCountByCustomerId(customerId);
    }

    @GetMapping("/customer/total-spend")
    @ResponseBody
    public double getTotalSpendByCustomer(@RequestParam Integer customerId) {
        return analyticsService.getTotalSpendByCustomerId(customerId);
    }
}
