package com.cosmetics.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cosmetics.entity.Supplier;
import com.cosmetics.repository.SupplierRepository;
import com.cosmetics.service.SupplierService;

@Service
@Transactional(readOnly = true)
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    
    @Autowired
    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }
    
    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }
    
    @Override
    public List<Supplier> findAll() {
        return getAllSuppliers();
    }

    @Override
    public Supplier findById(Integer id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid supplier ID: " + id));
    }

    @Override
    @Transactional
    public Supplier createSupplier(String name, String contactInfo, String address) {
        Supplier supplier = new Supplier();
        supplier.setName(name);
        
        // split contactInfo into email and phone (assuming format: "email|phone")
        if (contactInfo != null && contactInfo.contains("|")) {
            String[] parts = contactInfo.split("\\|", 2);
            supplier.setEmail(parts[0]);
            supplier.setPhone(parts[1]);
        } else {
            supplier.setEmail(contactInfo); // deafult to using as email
        }
        
        supplier.setAddress(address);
        return supplierRepository.save(supplier);
    }

    @Override
    @Transactional
    public Supplier updateSupplier(Integer id, String name, String contactInfo, String address) {
        Supplier supplier = findById(id);
        supplier.setName(name);
        
        // split contactInfo into email and phone (assuming format: "email|phone")
        if (contactInfo != null && contactInfo.contains("|")) {
            String[] parts = contactInfo.split("\\|", 2);
            supplier.setEmail(parts[0]);
            supplier.setPhone(parts[1]);
        } else {
            supplier.setEmail(contactInfo); // default to using as email
        }
        
        supplier.setAddress(address);
        return supplierRepository.save(supplier);
    }

    @Override
    @Transactional
    public void deleteSupplier(Integer id) {
        Supplier supplier = findById(id);
        supplierRepository.delete(supplier);
    }
}
