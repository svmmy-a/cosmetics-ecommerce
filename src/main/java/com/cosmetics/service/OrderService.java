package com.cosmetics.service;

import com.cosmetics.entity.Address;
import com.cosmetics.entity.Customer;
import com.cosmetics.entity.Order;

import java.util.List;

/**
 * Service interface for handling order operations.
 * Focuses on core order processing functionality.
 */
public interface OrderService {
    /**
     * Create a new order from an order object
     */
    Order createOrder(Order order);
    
    /**
     * Create a new order from customer, address and cart items
     */
    Order createOrder(Customer customer, Address address, List<Object> cartItems);
    
    /**
     * Update the status of an existing order
     */
    Order updateOrderStatus(Integer orderId, Order.OrderStatus status);
    
    /**
     * Find an order by its ID
     */
    Order findById(Integer orderId);
    
    /**
     * Find all orders for a specific customer
     */
    List<Order> findByCustomer(Customer customer);
    
    /**
     * Get all orders in the system
     */
    List<Order> getAllOrders();
}
