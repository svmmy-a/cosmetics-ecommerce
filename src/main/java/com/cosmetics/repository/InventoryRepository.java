package com.cosmetics.repository;

import com.cosmetics.entity.Inventory;
import com.cosmetics.entity.Product;
import com.cosmetics.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    List<Inventory> findByProduct(Product product);
    List<Inventory> findByWarehouse(Warehouse warehouse);
    Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse);
}
