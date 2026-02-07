package com.cottondealer.app.controller;

import com.cottondealer.app.model.CustomerMaster;
import com.cottondealer.app.model.CustomerPayment;
import com.cottondealer.app.model.CustomerPurchase;
import com.cottondealer.app.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String listCustomers(Model model, @RequestParam(required = false) String keyword) {
        if (keyword != null) {
            model.addAttribute("customers", customerService.searchCustomers(keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("customers", customerService.getAllCustomers());
        }
        model.addAttribute("newCustomer", new CustomerMaster());
        return "customer";
    }

    @PostMapping
    public String saveCustomer(@ModelAttribute CustomerMaster customer) {
        customerService.saveCustomer(customer);
        return "redirect:/customers";
    }

    @GetMapping("/{id}")
    public String customerDetail(@PathVariable Long id, Model model) {
        CustomerMaster customer = customerService.getCustomerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer Id:" + id));
        model.addAttribute("customer", customer);
        model.addAttribute("purchases", customerService.getPurchasesByCustomerId(id));
        model.addAttribute("payments", customerService.getPaymentsByCustomerId(id));
        model.addAttribute("totalPurchase", customerService.calculateTotalPurchaseAmount(id));
        model.addAttribute("totalPaid", customerService.calculateTotalPaidAmount(id));
        model.addAttribute("balance",
                customerService.calculateTotalPurchaseAmount(id) - customerService.calculateTotalPaidAmount(id));

        // Forms for new purchase and payment
        CustomerPurchase purchase = new CustomerPurchase();
        purchase.setCustomer(customer);
        purchase.setBuyDate(java.time.LocalDate.now());
        model.addAttribute("newPurchase", purchase);

        CustomerPayment payment = new CustomerPayment();
        payment.setCustomer(customer);
        payment.setPaymentDate(java.time.LocalDate.now());
        model.addAttribute("newPayment", payment);

        return "customer_detail";
    }

    @GetMapping("/bill/{purchaseId}")
    public String printBill(@PathVariable Long purchaseId, Model model) {
        CustomerPurchase purchase = customerService.getPurchaseById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid purchase Id:" + purchaseId));

        Long customerId = purchase.getCustomer().getCustomerId();
        double totalPurchase = customerService.calculateTotalPurchaseAmount(customerId);
        double totalPaid = customerService.calculateTotalPaidAmount(customerId);
        double balance = totalPurchase - totalPaid;

        // Calculate "Previous Balance" (Balance before this current specific bill is
        // technically hard to do perfectly without date sorting,
        // but typically users want the *current total outstanding* on the bill so they
        // know what to pay total)
        // We will show the Current Net Balance (which includes this bill).

        model.addAttribute("purchase", purchase);

        // Filter history: Date <= Current Bill Date
        // Filter history: Date <= Current Bill Date (Handle NULL dates safely)
        java.time.LocalDate billDate = purchase.getBuyDate() != null ? purchase.getBuyDate()
                : java.time.LocalDate.now();

        var history = customerService.getPurchasesByCustomerId(customerId).stream()
                .filter(p -> p.getBuyDate() != null && !p.getBuyDate().isAfter(billDate))
                .sorted((p1, p2) -> p2.getBuyDate().compareTo(p1.getBuyDate())) // Newest first
                .collect(java.util.stream.Collectors.toList());

        // Calculate Historical Balance (Snapshot at that date)
        double histPurchaseTotal = history.stream()
                .mapToDouble(p -> p.getTotalPrice() != null ? p.getTotalPrice() : 0.0).sum();

        double histPaidTotal = customerService.getPaymentsByCustomerId(customerId).stream()
                .filter(p -> p.getPaymentDate() != null && !p.getPaymentDate().isAfter(billDate))
                .mapToDouble(p -> p.getAmountPaid() != null ? p.getAmountPaid() : 0.0)
                .sum();

        double historicalBalance = histPurchaseTotal - histPaidTotal;

        // Use Global Totals for Balance Pending calculation (as requested by user)
        // Logic: Balance Pending = Total Purchased (All Time) - Total Paid (All Time)

        model.addAttribute("purchases", history);
        model.addAttribute("customer", purchase.getCustomer());
        model.addAttribute("totalBalance", balance); // Global Balance
        model.addAttribute("totalPaid", totalPaid); // Global Paid
        model.addAttribute("totalPurchase", totalPurchase); // Global Purchase
        model.addAttribute("billDate", billDate);
        return "print_bill";
    }

    @PostMapping("/purchase")
    public String savePurchase(@ModelAttribute CustomerPurchase purchase) {
        customerService.savePurchase(purchase);
        return "redirect:/customers/" + purchase.getCustomer().getCustomerId();
    }

    @PostMapping("/payment")
    public String savePayment(@ModelAttribute CustomerPayment payment) {
        customerService.savePayment(payment);
        return "redirect:/customers/" + payment.getCustomer().getCustomerId();
    }
}
