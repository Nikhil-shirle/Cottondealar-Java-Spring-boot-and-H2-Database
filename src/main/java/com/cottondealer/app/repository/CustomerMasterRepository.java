package com.cottondealer.app.repository;

import com.cottondealer.app.model.CustomerMaster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerMasterRepository extends JpaRepository<CustomerMaster, Long> {
    java.util.List<CustomerMaster> findByCustomerNameContainingIgnoreCaseOrAddressContainingIgnoreCase(String name,
            String address);
}
