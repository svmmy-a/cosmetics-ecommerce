package com.cosmetics.service.impl;

import com.cosmetics.dto.CustomerDto;
import com.cosmetics.entity.Customer;
import com.cosmetics.repository.CustomerRepository;
import com.cosmetics.service.CustomerService;
import com.cosmetics.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private OrderService orderService;

    @Override
    public Customer registerCustomer(Customer customer) throws Exception {
        if (emailExists(customer.getEmail())) {
            throw new Exception("Email already registered");
        }
        // hash password before saving
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return customerRepository.save(customer);
    }

    @Override
    public boolean emailExists(String email) {
        return customerRepository.findByEmail(email).isPresent();
    }

    @Override
    public Customer authenticate(String email, String password) throws Exception {
        Optional<Customer> customerOptional = customerRepository.findByEmail(email);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            if (passwordEncoder.matches(password, customer.getPassword())) {
                return customer;
            }
        }
        throw new Exception("Invalid email or password");
    }

    @Override
    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Customer findById(Integer id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public long countTotalCustomers() {
        return customerRepository.count();
    }

    @Override
    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email).orElse(null);
    }
    
    @Override
    public List<CustomerDto> getAllCustomersWithStats() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDto> customerDtos = new ArrayList<>();
        for (Customer customer : customers) {
            long orderCount = orderService.getOrderCountByCustomerId(customer.getCustomerId());
            double totalSpend = orderService.getTotalSpendByCustomerId(customer.getCustomerId());
            LocalDate lastOrderDate = orderService.getLastOrderDateByCustomerId(customer.getCustomerId());
            customerDtos.add(new CustomerDto(
                customer.getCustomerId(),
                customer.getFullName(),
                customer.getEmail(),
                orderCount,
                totalSpend,
                lastOrderDate
            ));
        }
        return customerDtos;
    }
}
