package com.cosmetics.service.impl;

import com.cosmetics.entity.Admin;
import com.cosmetics.repository.AdminRepository;
import com.cosmetics.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    @Override
    public boolean authenticate(String email, String password) {
        Optional<Admin> adminOptional = findByEmail(email);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            return admin.getPassword().equals(password);
        }
        return false;
    }
}
