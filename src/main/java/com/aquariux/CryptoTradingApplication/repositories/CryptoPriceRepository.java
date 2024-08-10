package com.aquariux.CryptoTradingApplication.repositories;

import com.aquariux.CryptoTradingApplication.entities.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Long> {

}
