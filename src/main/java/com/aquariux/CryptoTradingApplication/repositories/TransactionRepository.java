package com.aquariux.CryptoTradingApplication.repositories;

import com.aquariux.CryptoTradingApplication.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId")
    Optional<List<Transaction>> findAllByUserId(Long userId);

}
