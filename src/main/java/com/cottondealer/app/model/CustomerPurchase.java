package com.cottondealer.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "customer_purchase")
public class CustomerPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerMaster customer;

    private LocalDate buyDate;
    private String cottonType;
    private Double quantityKg;
    private Double pricePerKg;
    private Double totalPrice;

    // Payment details
    private String paymentType; // Cash, Check, Online
    private String checkNumber;
    private String bankAccountNo;
    private String onlineApp; // GPay, PhonePe, Paytm

    // Getters and Setters
    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public CustomerMaster getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerMaster customer) {
        this.customer = customer;
    }

    public LocalDate getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(LocalDate buyDate) {
        this.buyDate = buyDate;
    }

    public String getCottonType() {
        return cottonType;
    }

    public void setCottonType(String cottonType) {
        this.cottonType = cottonType;
    }

    public Double getQuantityKg() {
        return quantityKg;
    }

    public void setQuantityKg(Double quantityKg) {
        this.quantityKg = quantityKg;
    }

    public Double getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(Double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getBankAccountNo() {
        return bankAccountNo;
    }

    public void setBankAccountNo(String bankAccountNo) {
        this.bankAccountNo = bankAccountNo;
    }

    public String getOnlineApp() {
        return onlineApp;
    }

    public void setOnlineApp(String onlineApp) {
        this.onlineApp = onlineApp;
    }
}
