package com.cosmetics.config;

import com.cosmetics.entity.Admin;
import com.cosmetics.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Admin> adminOptional = adminRepository.findByEmail(email);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            return User.builder()
                    .username(admin.getEmail())
                    .password(admin.getPassword())
                    .roles("ADMIN")
                    .build();
        } else {
            throw new UsernameNotFoundException("Admin not found with email: " + email);
        }
    }
}
