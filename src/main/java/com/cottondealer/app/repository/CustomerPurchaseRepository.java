package com.cottondealer.app.repository;

import com.cottondealer.app.model.CustomerPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CustomerPurchaseRepository extends JpaRepository<CustomerPurchase, Long> {
    List<CustomerPurchase> findByCustomerCustomerId(Long customerId);
}
