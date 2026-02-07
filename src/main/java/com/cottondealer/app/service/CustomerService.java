package com.cottondealer.app.service;

import com.cottondealer.app.model.*;
import com.cottondealer.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerMasterRepository customerMasterRepository;
    @Autowired
    private CustomerPurchaseRepository purchaseRepository;
    @Autowired
    private CustomerPaymentRepository paymentRepository;

    public List<CustomerMaster> getAllCustomers() {
        return customerMasterRepository.findAll();
    }

    public List<CustomerMaster> searchCustomers(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return customerMasterRepository.findByCustomerNameContainingIgnoreCaseOrAddressContainingIgnoreCase(keyword,
                    keyword);
        }
        return customerMasterRepository.findAll();
    }

    public CustomerMaster saveCustomer(CustomerMaster customer) {
        return customerMasterRepository.save(customer);
    }

    public Optional<CustomerMaster> getCustomerById(Long id) {
        return customerMasterRepository.findById(id);
    }

    public CustomerPurchase savePurchase(CustomerPurchase purchase) {
        // Calculate total price automatically if not set
        if (purchase.getTotalPrice() == null && purchase.getQuantityKg() != null && purchase.getPricePerKg() != null) {
            purchase.setTotalPrice(purchase.getQuantityKg() * purchase.getPricePerKg());
        }
        return purchaseRepository.save(purchase);
    }

    public java.util.Optional<CustomerPurchase> getPurchaseById(Long id) {
        return purchaseRepository.findById(id);
    }

    public List<CustomerPurchase> getPurchasesByCustomerId(Long customerId) {
        return purchaseRepository.findByCustomerCustomerId(customerId);
    }

    public CustomerPayment savePayment(CustomerPayment payment) {
        return paymentRepository.save(payment);
    }

    public List<CustomerPayment> getPaymentsByCustomerId(Long customerId) {
        return paymentRepository.findByCustomerCustomerId(customerId);
    }

    public double calculateTotalPurchaseAmount(Long customerId) {
        return getPurchasesByCustomerId(customerId).stream()
                .mapToDouble(p -> p.getTotalPrice() != null ? p.getTotalPrice() : 0.0)
                .sum();
    }

    public double calculateTotalPaidAmount(Long customerId) {
        return getPaymentsByCustomerId(customerId).stream()
                .mapToDouble(p -> p.getAmountPaid() != null ? p.getAmountPaid() : 0.0)
                .sum();
    }
}
