package com.cottondealer.app.controller;

import com.cottondealer.app.service.CustomerService;
import com.cottondealer.app.service.GinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

        @Autowired
        private CustomerService customerService;
        @Autowired
        private GinService ginService;

        @GetMapping
        public String listTransactions(Model model,
                        @org.springframework.web.bind.annotation.RequestParam(required = false) java.time.LocalDate searchDate,
                        @org.springframework.web.bind.annotation.RequestParam(required = false) String searchMonth,
                        @org.springframework.web.bind.annotation.RequestParam(required = false) Double searchPrice,
                        @org.springframework.web.bind.annotation.RequestParam(required = false) String searchAddress,
                        @org.springframework.web.bind.annotation.RequestParam(required = false) java.time.LocalDate saleDate,
                        @org.springframework.web.bind.annotation.RequestParam(required = false) String saleMonth,
                        @org.springframework.web.bind.annotation.RequestParam(required = false) String saleGinName,
                        @org.springframework.web.bind.annotation.RequestParam(required = false) Double salePrice) {

                // --- PURCHASE LOGIC ---
                // Fetch all purchases
                var allPurchases = customerService.getAllCustomers().stream()
                                .flatMap(c -> customerService.getPurchasesByCustomerId(c.getCustomerId()).stream())
                                .collect(java.util.stream.Collectors.toList());

                // Apply Purchase Filters
                var filteredPurchases = allPurchases.stream()
                                .filter(p -> {
                                        boolean match = true;
                                        if (searchDate != null)
                                                match &= p.getBuyDate().equals(searchDate);
                                        if (searchMonth != null && !searchMonth.isEmpty()) {
                                                String pMonth = p.getBuyDate().toString().substring(0, 7);
                                                match &= pMonth.equals(searchMonth);
                                        }
                                        if (searchPrice != null)
                                                match &= p.getPricePerKg().equals(searchPrice);
                                        if (searchAddress != null && !searchAddress.isEmpty()) {
                                                match &= p.getCustomer().getAddress().toLowerCase()
                                                                .contains(searchAddress.toLowerCase());
                                        }
                                        return match;
                                })
                                .collect(java.util.stream.Collectors.toList());

                // --- SALES LOGIC ---
                var allSales = ginService.getAllSales();

                // Apply Sales Filters
                var filteredSales = allSales.stream()
                                .filter(s -> {
                                        boolean match = true;
                                        if (saleDate != null)
                                                match &= s.getSaleDate().equals(saleDate);
                                        if (saleMonth != null && !saleMonth.isEmpty()) {
                                                String sMonth = s.getSaleDate().toString().substring(0, 7);
                                                match &= sMonth.equals(saleMonth);
                                        }
                                        if (saleGinName != null && !saleGinName.isEmpty()) {
                                                match &= s.getGinName().toLowerCase()
                                                                .contains(saleGinName.toLowerCase());
                                        }
                                        if (salePrice != null)
                                                match &= s.getSellPricePerKg().equals(salePrice);
                                        return match;
                                })
                                .collect(java.util.stream.Collectors.toList());

                double totalBoughtAmount = filteredPurchases.stream()
                                .mapToDouble(p -> p.getTotalPrice() != null ? p.getTotalPrice() : 0.0).sum();
                double totalSoldAmount = filteredSales.stream()
                                .mapToDouble(s -> s.getTotalPrice() != null ? s.getTotalPrice() : 0.0)
                                .sum();

                model.addAttribute("purchases", filteredPurchases);
                model.addAttribute("sales", filteredSales);
                model.addAttribute("totalBought", totalBoughtAmount);
                model.addAttribute("totalSold", totalSoldAmount);
                model.addAttribute("netProfit", totalSoldAmount - totalBoughtAmount);

                // Pass filters back
                model.addAttribute("searchDate", searchDate);
                model.addAttribute("searchMonth", searchMonth);
                model.addAttribute("searchPrice", searchPrice);
                model.addAttribute("searchAddress", searchAddress);

                model.addAttribute("saleDate", saleDate);
                model.addAttribute("saleMonth", saleMonth);
                model.addAttribute("saleGinName", saleGinName);
                model.addAttribute("salePrice", salePrice);

                // Chart: Purchase (Cotton Type)
                java.util.Map<String, Double> purchaseChartData = new java.util.HashMap<>();
                for (var p : filteredPurchases) {
                        String type = p.getCottonType() != null ? p.getCottonType() : "Unknown";
                        purchaseChartData.put(type, purchaseChartData.getOrDefault(type, 0.0)
                                        + (p.getQuantityKg() != null ? p.getQuantityKg() : 0.0));
                }
                model.addAttribute("pieLabels", purchaseChartData.keySet());
                model.addAttribute("pieData", purchaseChartData.values());

                // Chart: Sales (Cotton Type)
                java.util.Map<String, Double> salesChartData = new java.util.HashMap<>();
                for (var s : filteredSales) {
                        String type = s.getCottonType() != null ? s.getCottonType() : "Unknown";
                        salesChartData.put(type, salesChartData.getOrDefault(type, 0.0)
                                        + (s.getQuantityKg() != null ? s.getQuantityKg() : 0.0));
                }
                model.addAttribute("salesPieLabels", salesChartData.keySet());
                model.addAttribute("salesPieData", salesChartData.values());

                return "transaction";
        }
}
