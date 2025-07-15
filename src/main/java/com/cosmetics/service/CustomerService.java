package com.cosmetics.service;

import com.cosmetics.dto.CustomerDto;
import com.cosmetics.entity.Customer;

import java.util.List;

public interface CustomerService {
    Customer registerCustomer(Customer customer) throws Exception;
    boolean emailExists(String email);
    Customer authenticate(String email, String password) throws Exception;
    Customer save(Customer customer);
    Customer findById(Integer id);
    long countTotalCustomers();
    Customer findByEmail(String email);
    List<CustomerDto> getAllCustomersWithStats();
}
