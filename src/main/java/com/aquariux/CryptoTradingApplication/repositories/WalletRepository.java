package com.aquariux.CryptoTradingApplication.repositories;

import com.aquariux.CryptoTradingApplication.Entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
