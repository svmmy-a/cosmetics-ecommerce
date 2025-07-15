package com.cosmetics.service;

import com.cosmetics.entity.Admin;

import java.util.Optional;

public interface AdminService {
    Optional<Admin> findByEmail(String email);
    boolean authenticate(String email, String password);
}
