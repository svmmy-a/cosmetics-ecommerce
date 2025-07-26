package com.cosmetics.service.impl;

import com.cosmetics.entity.Address;
import com.cosmetics.entity.Customer;
import com.cosmetics.service.AddressService;
import com.cosmetics.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of AddressService for handling address operations.
 */
@Service
public class AddressServiceImpl implements AddressService {

    private final CustomerService customerService;

    @Autowired
    public AddressServiceImpl(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public Address findExistingAddress(Integer addressId, Customer customer) {
        return customer.getAddresses().stream()
                .filter(a -> a.getAddressId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Selected address not found"));
    }

    @Override
    public Address createOrFindAddress(Customer customer, String addressLine, String city, String postcode, String country) {
        // First check if the address already exists to avoid duplicates
        return customer.getAddresses().stream()
                .filter(a -> a.getAddressLine().equals(addressLine) && a.getCity().equals(city)
                        && a.getPostcode().equals(postcode) && a.getCountry().equals(country))
                .findFirst()
                .orElseGet(() -> {
                    // Create new address if no duplicate is found
                    Address newAddress = new Address(customer, addressLine, city, postcode, country);
                    customer.addAddress(newAddress);
                    customerService.save(customer);
                    
                    // Ensure the address is persisted and retrieve it with ID
                    return customer.getAddresses().stream()
                            .filter(a -> a.getAddressLine().equals(addressLine) && a.getCity().equals(city)
                                && a.getPostcode().equals(postcode) && a.getCountry().equals(country))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("New address not persisted"));
                });
    }

    @Override
    public Customer saveCustomerWithAddress(Customer customer) {
        return customerService.save(customer);
    }
}
