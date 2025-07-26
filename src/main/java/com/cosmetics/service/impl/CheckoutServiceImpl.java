package com.cosmetics.service.impl;

import com.cosmetics.dto.CartItemDto;
import com.cosmetics.dto.CheckoutResultDto;
import com.cosmetics.entity.Address;
import com.cosmetics.entity.Customer;
import com.cosmetics.entity.Order;
import com.cosmetics.service.AddressService;
import com.cosmetics.service.CartService;
import com.cosmetics.service.CheckoutService;
import com.cosmetics.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of CheckoutService for handling checkout operations.
 */
@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final CartService cartService;
    private final OrderService orderService;
    private final AddressService addressService;

    @Autowired
    public CheckoutServiceImpl(CartService cartService, OrderService orderService, AddressService addressService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.addressService = addressService;
    }

    @Override
    public CheckoutResultDto calculateCartSummary(Customer customer) {
        CheckoutResultDto result = new CheckoutResultDto();
        
        if (customer != null) {
            List<Object> cartItemObjects = new ArrayList<>(cartService.getCartItemsForCustomer(customer));
            List<CartItemDto> cartItems = new ArrayList<>();
            
            // convert object list to CartItemDto list
            for (Object item : cartItemObjects) {
                if (item instanceof CartItemDto) {
                    cartItems.add((CartItemDto) item);
                }
            }
            
            // calculate cart summary
            double subtotal = cartItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
                
            double deliveryCost = subtotal >= 40.00 ? 0.00 : 5.00;
            double total = subtotal + deliveryCost;
            
            // set result values
            result.setItems(cartItems);
            result.setSavedAddresses(customer.getAddresses());
            result.setSubtotal(subtotal);
            result.setDeliveryCost(deliveryCost);
            result.setTotal(total);
        }
        
        return result;
    }

    @Override
    public Order processPayment(Customer customer, Integer addressId, 
                              String addressLine, String city, 
                              String postcode, String country) {
        if (customer == null) {
            return null;
        }
        
        // get the address to use (existing or new)
        Address addressToUse = getOrderAddress(customer, addressId, addressLine, city, postcode, country);
        
        // Create order with cart items
        List<Object> cartItems = cartService.getCartItemsForCustomer(customer);
        Order order = orderService.createOrder(customer, addressToUse, cartItems);
        
        // clear cart after successful order
        if (order != null) {
            cartService.clearCartForCustomer(customer);
        }
        
        return order;
    }

    @Override
    public Address getOrderAddress(Customer customer, Integer addressId, 
                               String addressLine, String city, 
                               String postcode, String country) {
        if (addressId != null) {
            // use existing address
            return addressService.findExistingAddress(addressId, customer);
        } else {
            // create new address or find matching existing address
            return addressService.createOrFindAddress(customer, addressLine, city, postcode, country);
        }
    }
}
