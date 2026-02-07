package com.cottondealer.app.repository;

import com.cottondealer.app.model.CustomerPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, Long> {
    List<CustomerPayment> findByCustomerCustomerId(Long customerId);
}
