package com.cottondealer.app.repository;

import com.cottondealer.app.model.GinSale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GinSaleRepository extends JpaRepository<GinSale, Long> {
    java.util.List<GinSale> findByGinNameContainingIgnoreCase(String name);
}
