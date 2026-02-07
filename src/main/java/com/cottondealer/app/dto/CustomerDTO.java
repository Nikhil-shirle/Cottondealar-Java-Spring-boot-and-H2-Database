package com.cottondealer.app.dto;

import com.cottondealer.app.model.Customer;

public class CustomerDTO {
    private Customer customer;
    private Double totalCottonAmount;
    private Double totalMoneyGiven; // Dealer -> Customer
    private Double totalMoneyTaken; // Customer -> Dealer
    private Double currentBalance; // Positive: Customer pays Dealer, Negative: Dealer pays Customer

    public CustomerDTO(Customer customer, Double totalCottonAmount, Double totalMoneyGiven, Double totalMoneyTaken) {
        this.customer = customer;
        this.totalCottonAmount = totalCottonAmount != null ? totalCottonAmount : 0.0;
        this.totalMoneyGiven = totalMoneyGiven != null ? totalMoneyGiven : 0.0;
        this.totalMoneyTaken = totalMoneyTaken != null ? totalMoneyTaken : 0.0;

        // Formula: Balance = (Money Given) - (Cotton Amount) - (Money Taken)
        // If Positive: We gave more than cotton value + what they paid back -> They owe
        // us.
        // If Negative: We gave less -> We owe them.
        this.currentBalance = this.totalMoneyGiven - this.totalCottonAmount - this.totalMoneyTaken;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Double getTotalCottonAmount() {
        return totalCottonAmount;
    }

    public void setTotalCottonAmount(Double totalCottonAmount) {
        this.totalCottonAmount = totalCottonAmount;
    }

    public Double getTotalMoneyGiven() {
        return totalMoneyGiven;
    }

    public void setTotalMoneyGiven(Double totalMoneyGiven) {
        this.totalMoneyGiven = totalMoneyGiven;
    }

    public Double getTotalMoneyTaken() {
        return totalMoneyTaken;
    }

    public void setTotalMoneyTaken(Double totalMoneyTaken) {
        this.totalMoneyTaken = totalMoneyTaken;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }
}
