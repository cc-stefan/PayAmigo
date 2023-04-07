package com.db.payamigo.service;

import com.db.payamigo.entity.Wallet;
import com.db.payamigo.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    private void validateWallet(Wallet wallet) {
        if (wallet.getName() == null || wallet.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Wallet name is required");
        }
        if (wallet.getBalance() <= 0) {
            throw new IllegalArgumentException("Balance cannot be negative or null");
        }
        if (wallet.getCurrency() == null) {
            throw new IllegalArgumentException("Currency is required");
        }
        if (wallet.getUser() == null) {
            throw new IllegalArgumentException("User is required");
        }
    }

    public Wallet createWallet(Wallet wallet) {
        validateWallet(wallet);
        return walletRepository.save(wallet);
    }

    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    public Optional<Wallet> getWalletById(int id) {
        return walletRepository.findById(id);
    }

    public Wallet updateWallet(int id, Wallet updatedWallet) {
        validateWallet(updatedWallet);
        return walletRepository.findById(id).map(wallet -> {
            wallet.setName(updatedWallet.getName());
            wallet.setBalance(updatedWallet.getBalance());
            wallet.setCurrency(updatedWallet.getCurrency());
            wallet.setUser(updatedWallet.getUser());
            return walletRepository.save(wallet);
        }).orElseThrow(() -> new RuntimeException("Wallet with id " + id + " not found!"));
    }

    public void deleteWallet(int id) {
        walletRepository.deleteById(id);
    }
}
