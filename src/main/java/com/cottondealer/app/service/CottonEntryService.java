package com.cottondealer.app.service;

import com.cottondealer.app.model.CottonEntry;
import com.cottondealer.app.repository.CottonEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CottonEntryService {

    @Autowired
    private CottonEntryRepository cottonEntryRepository;

    public CottonEntry saveCottonEntry(CottonEntry entry) {
        // Amount is calculated in PrePersist, but we can also ensure it here
        if (entry.getPricePerKg() != null && entry.getCottonQuantityKg() != null) {
            entry.setTotalAmount(entry.getPricePerKg() * entry.getCottonQuantityKg());
        }
        return cottonEntryRepository.save(entry);
    }

    public List<CottonEntry> getEntriesByCustomer(Long customerId) {
        return cottonEntryRepository.findByCustomerCustomerId(customerId);
    }
}
