package com.cottondealer.app.controller;

import com.cottondealer.app.model.CottonEntry;
import com.cottondealer.app.model.Customer;
import com.cottondealer.app.model.Transaction;
import com.cottondealer.app.repository.CottonEntryRepository;
import com.cottondealer.app.repository.CustomerRepository;
import com.cottondealer.app.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CottonEntryRepository cottonEntryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/dashboard-stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Customer> customers = customerRepository.findAll();
        List<CottonEntry> entries = cottonEntryRepository.findAll();

        // Basic Stats
        stats.put("totalCustomers", customers.size());

        double totalCottonKg = entries.stream()
                .mapToDouble(e -> e.getCottonQuantityKg() != null ? e.getCottonQuantityKg() : 0)
                .sum();
        stats.put("totalCottonKg", totalCottonKg);

        double totalAmount = entries.stream()
                .mapToDouble(e -> e.getTotalAmount() != null ? e.getTotalAmount() : 0)
                .sum();
        stats.put("totalAmount", totalAmount);

        // Top Villages (by Cotton Quantity)
        Map<String, Double> villageStats = new HashMap<>();
        for (CottonEntry entry : entries) {
            String village = entry.getCustomer().getVillage();
            if (village == null)
                village = "Unknown";
            double qty = entry.getCottonQuantityKg() != null ? entry.getCottonQuantityKg() : 0;
            villageStats.put(village, villageStats.getOrDefault(village, 0.0) + qty);
        }

        // Sort and take Top 5 Villages
        List<Map<String, Object>> topVillages = villageStats.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .map(e -> Map.of("village", (Object) e.getKey(), "qty", (Object) e.getValue()))
                .collect(Collectors.toList());
        stats.put("topVillages", topVillages);

        // Top Customers (by Total Value)
        Map<String, Double> customerStats = new HashMap<>();
        for (CottonEntry entry : entries) {
            String name = entry.getCustomer().getName();
            double amt = entry.getTotalAmount() != null ? entry.getTotalAmount() : 0;
            customerStats.put(name, customerStats.getOrDefault(name, 0.0) + amt);
        }

        List<Map<String, Object>> topCustomers = customerStats.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .map(e -> Map.of("name", (Object) e.getKey(), "amount", (Object) e.getValue()))
                .collect(Collectors.toList());
        stats.put("topCustomers", topCustomers);

        return stats;
    }
}
