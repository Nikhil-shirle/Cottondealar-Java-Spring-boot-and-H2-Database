package com.cottondealer.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cotton_entries")
public class CottonEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cottonEntryId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private String cottonType;

    private Double cottonQuantityKg; // replaced cottonWeight

    private Double pricePerKg; // replaced ratePerUnit

    private Double totalAmount;

    private LocalDate date;

    private String notes;

    public CottonEntry() {
    }

    public Long getCottonEntryId() {
        return cottonEntryId;
    }

    public void setCottonEntryId(Long cottonEntryId) {
        this.cottonEntryId = cottonEntryId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getCottonType() {
        return cottonType;
    }

    public void setCottonType(String cottonType) {
        this.cottonType = cottonType;
    }

    public Double getCottonQuantityKg() {
        return cottonQuantityKg;
    }

    public void setCottonQuantityKg(Double cottonQuantityKg) {
        this.cottonQuantityKg = cottonQuantityKg;
    }

    public Double getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(Double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @PrePersist
    public void calculateAmount() {
        if (cottonQuantityKg != null && pricePerKg != null) {
            this.totalAmount = cottonQuantityKg * pricePerKg;
        }
        if (date == null) {
            date = LocalDate.now();
        }
    }
}
