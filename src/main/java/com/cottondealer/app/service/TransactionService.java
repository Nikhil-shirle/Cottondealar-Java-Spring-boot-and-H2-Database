package com.cottondealer.app.service;

import com.cottondealer.app.model.Transaction;
import com.cottondealer.app.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByCustomer(Long customerId) {
        return transactionRepository.findByCustomerCustomerId(customerId);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
