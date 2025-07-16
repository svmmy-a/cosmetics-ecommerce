package com.cosmetics.repository;

import com.cosmetics.entity.ProductSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSupplierRepository extends JpaRepository<ProductSupplier, Integer> {
    List<ProductSupplier> findByProduct_ProductId(Integer productId);
}
