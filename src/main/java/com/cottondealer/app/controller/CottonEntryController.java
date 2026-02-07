package com.cottondealer.app.controller;

import com.cottondealer.app.model.CottonEntry;
import com.cottondealer.app.repository.CottonEntryRepository;
import com.cottondealer.app.service.CottonEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/entries")
public class CottonEntryController {

    @Autowired
    private CottonEntryService cottonEntryService;

    @Autowired
    private CottonEntryRepository cottonEntryRepository;

    @PostMapping
    public CottonEntry addCottonEntry(@RequestBody CottonEntry entry) {
        return cottonEntryService.saveCottonEntry(entry);
    }

    @GetMapping("/customer/{customerId}")
    public List<CottonEntry> getEntriesByCustomer(@PathVariable Long customerId) {
        return cottonEntryService.getEntriesByCustomer(customerId);
    }

    // New Endpoint for Cotton Records Page (Filtering & Sorting)
    @GetMapping("/all")
    public List<CottonEntry> getAllEntries(
            @RequestParam(required = false) String filterBy,
            @RequestParam(required = false) String filterValue,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);

        if (filterBy != null && filterValue != null && !filterValue.isEmpty()) {
            switch (filterBy) {
                case "name":
                    return cottonEntryRepository.findByCustomerNameContainingIgnoreCase(filterValue, sort);
                case "village":
                    return cottonEntryRepository.findByCustomerVillageContainingIgnoreCase(filterValue, sort);
                case "type":
                    return cottonEntryRepository.findByCottonTypeContainingIgnoreCase(filterValue, sort);
            }
        }

        if (startDate != null && endDate != null) {
            return cottonEntryRepository.findByDateBetween(
                    LocalDate.parse(startDate),
                    LocalDate.parse(endDate),
                    sort);
        }

        return cottonEntryRepository.findAll(sort);
    }
}
