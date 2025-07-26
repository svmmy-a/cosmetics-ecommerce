package com.cosmetics.service;

import com.cosmetics.dto.CheckoutResultDto;
import com.cosmetics.entity.Address;
import com.cosmetics.entity.Customer;
import com.cosmetics.entity.Order;

/**
 * Service interface for handling checkout operations.
 * Separates checkout logic from controller.
 */
public interface CheckoutService {
    
    /**
     * Calculates cart summary for display on checkout/payment pages.
     * 
     * @param customer the customer whose cart to summarize
     * @return checkout result with cart summary details
     */
    CheckoutResultDto calculateCartSummary(Customer customer);
    
    /**
     * Processes payment and creates an order using either an existing address or new address data.
     * 
     * @param customer the customer making the order
     * @param addressId optional ID of existing address to use
     * @param addressLine line 1 of address if creating new address
     * @param city city if creating new address
     * @param postcode postal/zip code if creating new address
     * @param country country if creating new address
     * @return order created or null if failed
     */
    Order processPayment(Customer customer, Integer addressId, 
                        String addressLine, String city, 
                        String postcode, String country);
    
    /**
     * Get address to use for order, either existing or new.
     * 
     * @param customer the customer making the order
     * @param addressId optional ID of existing address to use
     * @param addressLine line 1 of address if creating new address
     * @param city city if creating new address
     * @param postcode postal/zip code if creating new address
     * @param country country if creating new address
     * @return address to use for order
     */
    Address getOrderAddress(Customer customer, Integer addressId, 
                         String addressLine, String city, 
                         String postcode, String country);
}
