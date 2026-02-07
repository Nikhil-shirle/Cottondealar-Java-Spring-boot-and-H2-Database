package com.cottondealer.app.repository;

import com.cottondealer.app.model.CottonEntry;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CottonEntryRepository extends JpaRepository<CottonEntry, Long> {
    List<CottonEntry> findByCustomerCustomerId(Long customerId);

    // Advanced Filtering with Sorting
    List<CottonEntry> findByCustomerNameContainingIgnoreCase(String name, Sort sort);

    List<CottonEntry> findByCustomerVillageContainingIgnoreCase(String village, Sort sort);

    List<CottonEntry> findByCottonTypeContainingIgnoreCase(String cottonType, Sort sort);

    List<CottonEntry> findByDateBetween(LocalDate startDate, LocalDate endDate, Sort sort);
}
