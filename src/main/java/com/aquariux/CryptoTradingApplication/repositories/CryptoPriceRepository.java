package com.aquariux.CryptoTradingApplication.repositories;

import com.aquariux.CryptoTradingApplication.entities.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Long> {

    @Query(value = "SELECT * FROM crypto_price WHERE crypto_pair = :ticker ORDER BY time_stamp DESC LIMIT 1", nativeQuery = true)
    Optional<CryptoPrice> findLatestPriceByTicker(String ticker);

    @Query(value = "SELECT * FROM crypto_price cp1 WHERE cp1.time_stamp = (SELECT MAX(cp2.time_stamp) FROM crypto_price cp2 WHERE cp2.crypto_pair = cp1.crypto_pair)", nativeQuery = true)
    Optional<List<CryptoPrice>> findLatestPriceForAllTickers();

}
