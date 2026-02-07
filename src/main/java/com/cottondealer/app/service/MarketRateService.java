package com.cottondealer.app.service;

import com.cottondealer.app.model.CottonMarketRate;
import com.cottondealer.app.repository.CottonMarketRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class MarketRateService {
    @Autowired
    private CottonMarketRateRepository rateRepository;

    public Optional<CottonMarketRate> getRateByDate(LocalDate date) {
        return rateRepository.findByRateDate(date);
    }

    public CottonMarketRate saveRate(CottonMarketRate rate) {
        return rateRepository.save(rate);
    }

    public CottonMarketRate getTodayRate() {
        return getRateByDate(LocalDate.now()).orElse(null);
    }
}
