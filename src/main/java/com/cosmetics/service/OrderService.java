package com.cosmetics.service;

import com.cosmetics.entity.Address;
import com.cosmetics.entity.Customer;
import com.cosmetics.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderService {
    Order createOrder(Order order);
    Order createOrder(Customer customer, Address address, List<Object> cartItems);
    Order updateOrderStatus(Integer orderId, Order.OrderStatus status);
    Order findById(Integer orderId);
    List<Order> findByCustomer(Customer customer);
    List<Order> getAllOrders();
    long countTotalOrders();
    long countPendingDeliveries();
    BigDecimal calculateTotalRevenue();
    double calculateRevenueGrowthPercentage();
    double calculateOrdersGrowthPercentage();
    List<Map<String, Object>> getSalesDataForRange(String range);
    List<Map<String, Object>> getSalesDataLast30Days();
    List<Map<String, Object>> getTopSellingProducts();
    List<Map<String, Object>> getTopSellingProductsByRange(String range);
    List<Map<String, Object>> getAllProductSalesData();
    BigDecimal calculateRevenueForCurrentMonth();
    BigDecimal calculateRevenueForPreviousMonth();
    long countOrdersForCurrentMonth();
    long countOrdersForPreviousMonth();
    long getOrderCountByCustomerId(Integer customerId);
    double getTotalSpendByCustomerId(Integer customerId);
    LocalDate getLastOrderDateByCustomerId(Integer customerId);
}
