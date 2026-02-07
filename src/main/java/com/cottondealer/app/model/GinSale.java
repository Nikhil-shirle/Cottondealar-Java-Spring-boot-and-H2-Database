package com.cottondealer.app.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "gin_sale")
public class GinSale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long saleId;

    private String ginName;
    private String cottonType;
    private Double quantityKg;
    private Double sellPricePerKg;
    private Double totalPrice;
    private LocalDate saleDate;

    // Getters and Setters
    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public String getGinName() {
        return ginName;
    }

    public void setGinName(String ginName) {
        this.ginName = ginName;
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

    public Double getSellPricePerKg() {
        return sellPricePerKg;
    }

    public void setSellPricePerKg(Double sellPricePerKg) {
        this.sellPricePerKg = sellPricePerKg;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }
}
