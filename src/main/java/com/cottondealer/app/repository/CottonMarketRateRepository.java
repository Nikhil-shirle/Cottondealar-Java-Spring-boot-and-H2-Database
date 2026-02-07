package com.cottondealer.app.repository;

import com.cottondealer.app.model.CottonMarketRate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface CottonMarketRateRepository extends JpaRepository<CottonMarketRate, Long> {
    Optional<CottonMarketRate> findByRateDate(LocalDate date);
}
