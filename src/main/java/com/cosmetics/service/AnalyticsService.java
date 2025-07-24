package com.cosmetics.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service for handling analytics-related operations.
 * This separates analytics logic from core order processing functionality.
 */
public interface AnalyticsService {
    
    /**
     * Calculate total revenue across all orders
     * @return the total revenue
     */
    BigDecimal calculateTotalRevenue();
    
    /**
     * Calculate revenue for the current month
     * @return the current month's revenue
     */
    BigDecimal calculateRevenueForCurrentMonth();
    
    /**
     * Calculate revenue for the previous month
     * @return the previous month's revenue
     */
    BigDecimal calculateRevenueForPreviousMonth();
    
    /**
     * Calculate revenue growth percentage between current and previous month
     * @return the growth percentage
     */
    double calculateRevenueGrowthPercentage();
    
    /**
     * Count total orders in the system
     * @return the total order count
     */
    long countTotalOrders();
    
    /**
     * Count orders with pending status
     * @return the count of pending deliveries
     */
    long countPendingDeliveries();
    
    /**
     * Count orders for the current month
     * @return the current month's order count
     */
    long countOrdersForCurrentMonth();
    
    /**
     * Count orders for the previous month
     * @return the previous month's order count
     */
    long countOrdersForPreviousMonth();
    
    /**
     * Calculate order growth percentage between current and previous month
     * @return the growth percentage
     */
    double calculateOrdersGrowthPercentage();
    
    /**
     * Get the top selling products overall
     * @return list of top products with sales data
     */
    List<Map<String, Object>> getTopSellingProducts();
    
    /**
     * Get the top selling products by time range
     * @param range the time range ("weekly", "monthly", etc.)
     * @return list of top products with sales data
     */
    List<Map<String, Object>> getTopSellingProductsByRange(String range);
    
    /**
     * Get aggregated sales data for all products
     * @return list of all products with sales data
     */
    List<Map<String, Object>> getAllProductSalesData();
    
    /**
     * Get sales data for the last 30 days
     * @return list of sales data by time period
     */
    List<Map<String, Object>> getSalesDataLast30Days();
    
    /**
     * Get sales data for a specific time range
     * @param range the time range ("today", "last7days", "last28days", "last3months")
     * @return list of sales data by time period
     */
    List<Map<String, Object>> getSalesDataForRange(String range);
    
    /**
     * Get the number of orders placed by a specific customer
     * @param customerId the customer ID
     * @return the order count for this customer
     */
    long getOrderCountByCustomerId(Integer customerId);
    
    /**
     * Calculate the total amount spent by a specific customer
     * @param customerId the customer ID
     * @return the total amount spent
     */
    double getTotalSpendByCustomerId(Integer customerId);
    
    /**
     * Get the date of the last order placed by a specific customer
     * @param customerId the customer ID
     * @return the last order date
     */
    LocalDate getLastOrderDateByCustomerId(Integer customerId);
}
