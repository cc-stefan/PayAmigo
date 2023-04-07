package com.db.payamigo.service;

import com.db.payamigo.entity.Transaction;
import com.db.payamigo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    private void validateTransaction(Transaction transaction) {
        if (transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Transaction amount cannot be negative or null");
        }
        if (transaction.getSourceWallet() != null && (transaction.getAmount() > transaction.getSourceWallet().getBalance())) {
            throw new IllegalArgumentException("Transaction amount higher than source wallet balance");
        }
        if (transaction.getCommissionPercent() < 0) {
            throw new IllegalArgumentException("Commission percent cannot be negative");
        }
        if (transaction.getCommissionAmount() < 0) {
            throw new IllegalArgumentException("Commission amount cannot be negative");
        }
        if (transaction.getCurrency() == null) {
            throw new IllegalArgumentException("Currency is required");
        }
        if (transaction.getCreatedAt() == null) {
            throw new IllegalArgumentException("Transaction date is required");
        }
        if (transaction.getCreatedAt().before(new Date(System.currentTimeMillis() - 1000))) {
            throw new IllegalArgumentException("Transaction date cannot be in the past");
        }
        if (transaction.getSourceWallet() == null) {
            throw new IllegalArgumentException("Source wallet is required");
        }
        if (transaction.getDestinationWallet() == null) {
            throw new IllegalArgumentException("Destination wallet is required");
        }
        if (transaction.getDestinationWallet() == transaction.getSourceWallet()) {
            throw new IllegalArgumentException("User cannot pay itself");
        }
    }

    public Transaction createTransaction(Transaction transaction) {
        validateTransaction(transaction);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(int id) {
        return transactionRepository.findById(id);
    }

    public Transaction updateTransaction(int id, Transaction updatedTransaction) {
        validateTransaction(updatedTransaction);
        return transactionRepository.findById(id).map(transaction -> {
            transaction.setAmount(updatedTransaction.getAmount());
            transaction.setCommissionPercent(updatedTransaction.getCommissionPercent());
            transaction.setCommissionAmount(updatedTransaction.getCommissionAmount());
            transaction.setCurrency(updatedTransaction.getCurrency());
            transaction.setCreatedAt(updatedTransaction.getCreatedAt());
            transaction.setSourceWallet(updatedTransaction.getSourceWallet());
            transaction.setDestinationWallet(updatedTransaction.getDestinationWallet());
            return transactionRepository.save(transaction);
        }).orElseThrow(() -> new RuntimeException("Transaction with id " + id + " not found!"));
    }

    public void deleteTransaction(int id) {
        transactionRepository.deleteById(id);
    }
}
