package com.cottondealer.app.controller;

import com.cottondealer.app.service.MarketRateService;
import com.cottondealer.app.service.CustomerService;
import com.cottondealer.app.service.GinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

        @Autowired
        private MarketRateService marketRateService;
        @Autowired
        private CustomerService customerService;
        @Autowired
        private GinService ginService;

        @GetMapping("/")
        public String home(Model model,
                        @org.springframework.web.bind.annotation.RequestParam(required = false) Double searchPrice,
                        @org.springframework.web.bind.annotation.RequestParam(required = false) String searchName,
                        @org.springframework.web.bind.annotation.RequestParam(required = false) String searchAddress) {

                model.addAttribute("todayRate", marketRateService.getTodayRate());

                // Fetch all customers & purchases
                var customers = customerService.getAllCustomers();
                var allPurchases = customers.stream()
                                .flatMap(c -> customerService.getPurchasesByCustomerId(c.getCustomerId()).stream())
                                .collect(java.util.stream.Collectors.toList());

                // Filter Purchases
                var filteredPurchases = allPurchases.stream()
                                .filter(p -> {
                                        boolean match = true;
                                        if (searchPrice != null)
                                                match &= (p.getPricePerKg() != null
                                                                && p.getPricePerKg().equals(searchPrice));
                                        if (searchName != null && !searchName.isEmpty()) {
                                                match &= (p.getCustomer().getCustomerName().toLowerCase()
                                                                .contains(searchName.toLowerCase()));
                                        }
                                        if (searchAddress != null && !searchAddress.isEmpty()) {
                                                match &= (p.getCustomer().getAddress().toLowerCase()
                                                                .contains(searchAddress.toLowerCase()));
                                        }
                                        return match;
                                })
                                .collect(java.util.stream.Collectors.toList());

                var sales = ginService.getAllSales();

                // Calculate Stats based on FILTERED purchases
                double totalPurchasedKg = filteredPurchases.stream()
                                .mapToDouble(p -> p.getQuantityKg() != null ? p.getQuantityKg() : 0.0).sum();
                double totalSoldKg = sales.stream()
                                .mapToDouble(s -> s.getQuantityKg() != null ? s.getQuantityKg() : 0.0).sum();

                // Note: 'Stock' logic might be weird if we filter purchases (e.g. filter by 1
                // customer -> stock looks negative?).
                // But user asked for "purchase analysis depends on...", so we filter the
                // displayed Purchasing Stats.
                // We will explicitly label or keep stock as global?
                // Typically "Stock" is global inventory. Filtering purchases doesn't change
                // physical stock.
                // However, for "Analysis", we might want to see "How much did I buy from X?".
                // Let's keep "Stock" as Global (Total Bought - Total Sold) to avoid confusion,
                // OR update it to (Filtered Bought - Total Sold).
                // A safer bet for a Dashboard is:
                // Cards 1 & 4 (Purchased & Profit) -> Filtered.
                // Cards 2 & 3 (Sold & Stock) -> Global (unless we also filter sales).
                // Let's calculate Global for Stock/Sold, and Filtered for
                // Purchased/Profit(partial).

                // Recalculate Global for Stock
                double globalPurchasedKg = allPurchases.stream()
                                .mapToDouble(p -> p.getQuantityKg() != null ? p.getQuantityKg() : 0.0).sum();

                double totalPurchaseCost = filteredPurchases.stream()
                                .mapToDouble(p -> p.getTotalPrice() != null ? p.getTotalPrice() : 0.0).sum();
                double totalSalesRevenue = sales.stream()
                                .mapToDouble(s -> s.getTotalPrice() != null ? s.getTotalPrice() : 0.0)
                                .sum();

                model.addAttribute("totalPurchasedKg", totalPurchasedKg); // Filtered
                model.addAttribute("totalSoldKg", totalSoldKg); // Global
                model.addAttribute("stockAvailable", globalPurchasedKg - totalSoldKg); // Global Stock

                // Net Profit here is tricky. (Global Revenue - Filtered Cost) doesn't make
                // sense.
                // Let's show (Global Revenue - Global Cost) as "Net Profit" is usually a
                // company-wide metric.
                // OR we hide Profit when filtering?
                // Let's keep Profit Global for now to avoid misleading data.
                double globalPurchaseCost = allPurchases.stream()
                                .mapToDouble(p -> p.getTotalPrice() != null ? p.getTotalPrice() : 0.0).sum();
                model.addAttribute("netProfit", totalSalesRevenue - globalPurchaseCost);

                // Chart Data Logic
                java.util.Map<String, Double> chartData = new java.util.HashMap<>();
                String chartTitle = "Stock Analysis (Global)"; // Default Title

                boolean isFiltered = (searchPrice != null || (searchName != null && !searchName.isEmpty())
                                || (searchAddress != null && !searchAddress.isEmpty()));

                if (!isFiltered) {
                        // DEFAULT: Show Stock Analysis (Global Purchases - Global Sales per Type)
                        // Aggregate Global Purchases
                        java.util.Map<String, Double> pMap = new java.util.HashMap<>();
                        for (var p : allPurchases) {
                                String type = p.getCottonType() != null ? p.getCottonType() : "Unknown";
                                pMap.put(type, pMap.getOrDefault(type, 0.0)
                                                + (p.getQuantityKg() != null ? p.getQuantityKg() : 0.0));
                        }
                        // Aggregate Global Sales
                        java.util.Map<String, Double> sMap = new java.util.HashMap<>();
                        for (var s : sales) {
                                String type = s.getCottonType() != null ? s.getCottonType() : "Unknown";
                                sMap.put(type, sMap.getOrDefault(type, 0.0)
                                                + (s.getQuantityKg() != null ? s.getQuantityKg() : 0.0));
                        }
                        // Calculate Stock (Purchase - Sale)
                        for (String type : pMap.keySet()) {
                                double bought = pMap.get(type);
                                double sold = sMap.getOrDefault(type, 0.0);
                                double stock = Math.max(0, bought - sold); // Prevent negative stock in chart
                                if (stock > 0) {
                                        chartData.put(type, stock);
                                }
                        }
                } else {
                        // FILTERED: Show Purchase Analysis
                        chartTitle = "Purchase Analysis (Filtered)";
                        for (var p : filteredPurchases) {
                                String type = p.getCottonType() != null ? p.getCottonType() : "Unknown";
                                chartData.put(type, chartData.getOrDefault(type, 0.0)
                                                + (p.getQuantityKg() != null ? p.getQuantityKg() : 0.0));
                        }
                }

                model.addAttribute("pieLabels", chartData.keySet());
                model.addAttribute("pieData", chartData.values());
                model.addAttribute("chartTitle", chartTitle);

                // Pass back params
                model.addAttribute("searchPrice", searchPrice);
                model.addAttribute("searchName", searchName);
                model.addAttribute("searchAddress", searchAddress);

                // Form for Rate
                model.addAttribute("newRate", new com.cottondealer.app.model.CottonMarketRate());
                return "home";
        }

        @org.springframework.web.bind.annotation.PostMapping("/rate")
        public String setRate(
                        @org.springframework.web.bind.annotation.ModelAttribute com.cottondealer.app.model.CottonMarketRate rate) {
                rate.setRateDate(java.time.LocalDate.now());
                marketRateService.saveRate(rate);
                return "redirect:/";
        }

        @GetMapping("/login")
        public String login() {
                return "login";
        }
}
