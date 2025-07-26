package com.cosmetics.service;

import com.cosmetics.entity.Address;
import com.cosmetics.entity.Customer;

/**
 * Service interface for handling address operations.
 */
public interface AddressService {
    
    /**
     * Finds an existing address by ID.
     * 
     * @param addressId the ID of the address to find
     * @param customer the customer to validate ownership
     * @return the address if found and belongs to the customer
     * @throws IllegalStateException if address not found or doesn't belong to customer
     */
    Address findExistingAddress(Integer addressId, Customer customer);
    
    /**
     * Creates a new address for a customer or returns matching existing address.
     * 
     * @param customer the customer to create address for
     * @param addressLine line 1 of address
     * @param city city
     * @param postcode postal/zip code
     * @param country country
     * @return the created or existing matching address
     */
    Address createOrFindAddress(Customer customer, String addressLine, String city, String postcode, String country);
    
    /**
     * Saves a customer with updated address information.
     * 
     * @param customer the customer to save
     * @return the updated customer
     */
    Customer saveCustomerWithAddress(Customer customer);
}
