package com.aquariux.CryptoTradingApplication.repositories;

import com.aquariux.CryptoTradingApplication.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
