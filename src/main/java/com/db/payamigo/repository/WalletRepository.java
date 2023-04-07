package com.db.payamigo.repository;

import com.db.payamigo.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository  extends JpaRepository<Wallet, Integer> {
}
