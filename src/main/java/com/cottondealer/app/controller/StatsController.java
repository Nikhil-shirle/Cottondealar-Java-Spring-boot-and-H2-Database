package com.cottondealer.app.controller;

import com.cottondealer.app.service.CustomerService;
import com.cottondealer.app.service.GinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private GinService ginService;

    @GetMapping("/cotton-type")
    public Map<String, Double> getCottonTypeStats() {
        // Aggregate purchases by cotton type
        var purchases = customerService.getAllCustomers().stream()
                .flatMap(c -> customerService.getPurchasesByCustomerId(c.getCustomerId()).stream())
                .collect(Collectors.toList());

        Map<String, Double> stats = new HashMap<>();
        for (var p : purchases) {
            String type = p.getCottonType() != null ? p.getCottonType() : "Unknown";
            stats.put(type, stats.getOrDefault(type, 0.0) + (p.getQuantityKg() != null ? p.getQuantityKg() : 0.0));
        }
        return stats;
    }

    @GetMapping("/sales-log")
    public Map<String, Double> getSalesLog() {
        // Aggregate sales by date
        var sales = ginService.getAllSales();

        Map<String, Double> stats = new HashMap<>();
        for (var s : sales) {
            if (s.getSaleDate() != null) {
                String date = s.getSaleDate().toString();
                stats.put(date, stats.getOrDefault(date, 0.0) + (s.getQuantityKg() != null ? s.getQuantityKg() : 0.0));
            }
        }
        return stats;
    }
}
