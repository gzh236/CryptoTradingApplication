package com.aquariux.CryptoTradingApplication.repositories;

import com.aquariux.CryptoTradingApplication.entities.CryptoPrice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.BTCUSDT;
import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.ETHUSDT;
import static com.aquariux.CryptoTradingApplication.testdata.CryptoPriceTestData.getMockCryptoPriceList;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
public class CryptoPriceRepositoryTests {

    @Autowired
    CryptoPriceRepository repository;

    @Test
    void shouldFindLatestPricesForTicker() {
        List<CryptoPrice> entityList = getMockCryptoPriceList(8);
        BigDecimal maxBidPrice = entityList.stream()
                        .filter(price -> BTCUSDT.equalsIgnoreCase(price.getCryptoPair()))
                        .map(CryptoPrice::getBidPrice)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

        BigDecimal maxAskPrice = entityList.stream()
                        .filter(price -> BTCUSDT.equalsIgnoreCase(price.getCryptoPair()))
                        .map(CryptoPrice::getAskPrice)
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

        LocalDateTime latestTimeStamp = entityList.stream()
                        .filter(price -> BTCUSDT.equalsIgnoreCase(price.getCryptoPair()))
                        .map(CryptoPrice::getTimeStamp)
                        .max(LocalDateTime::compareTo)
                        .orElse(LocalDateTime.MIN);

        repository.saveAll(entityList);

        Optional<CryptoPrice> optional = repository.findLatestPriceByTicker(BTCUSDT);

        assertTrue(optional.isPresent());

        CryptoPrice res = optional.get();

        assertNotNull(res);
        assertEquals(BTCUSDT, res.getCryptoPair());
        assertEquals(maxBidPrice, res.getBidPrice());
        assertEquals(maxAskPrice, res.getAskPrice());
        assertEquals(latestTimeStamp, res.getTimeStamp());

    }

    @Test
    void shouldFindLatestPriceForAllTickers() {
        List<CryptoPrice> entityList = getMockCryptoPriceList(8);

        BigDecimal maxBTCUSDTBidPrice = entityList.stream()
                .filter(price -> BTCUSDT.equalsIgnoreCase(price.getCryptoPair()))
                .map(CryptoPrice::getBidPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal maxBTCUSDTAskPrice = entityList.stream()
                .filter(price -> BTCUSDT.equalsIgnoreCase(price.getCryptoPair()))
                .map(CryptoPrice::getAskPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        LocalDateTime latestBTCUSDTTimeStamp = entityList.stream()
                .filter(price -> BTCUSDT.equalsIgnoreCase(price.getCryptoPair()))
                .map(CryptoPrice::getTimeStamp)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MIN);

        BigDecimal maxETHUSDTBidPrice = entityList.stream()
                .filter(price -> ETHUSDT.equalsIgnoreCase(price.getCryptoPair()))
                .map(CryptoPrice::getBidPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal maxETHUSDTAskPrice = entityList.stream()
                .filter(price -> ETHUSDT.equalsIgnoreCase(price.getCryptoPair()))
                .map(CryptoPrice::getAskPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        LocalDateTime latestETHUSDTTimeStamp = entityList.stream()
                .filter(price -> ETHUSDT.equalsIgnoreCase(price.getCryptoPair()))
                .map(CryptoPrice::getTimeStamp)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.MIN);

        repository.saveAll(entityList);

        Optional<List<CryptoPrice>> optional = repository.findLatestPriceForAllTickers();
        assertTrue(optional.isPresent());

        List<CryptoPrice> res = optional.get();
        assertNotNull(res);
        assertEquals(2, res.size());

        Optional<CryptoPrice> btcUsdtRes = res.stream()
                .filter(data -> BTCUSDT.equalsIgnoreCase(data.getCryptoPair()))
                .findFirst();

        assertTrue(btcUsdtRes.isPresent());

        CryptoPrice btcUsdt = btcUsdtRes.get();
        assertNotNull(btcUsdt);
        assertEquals(maxBTCUSDTBidPrice, btcUsdt.getBidPrice());
        assertEquals(maxBTCUSDTAskPrice, btcUsdt.getAskPrice());
        assertEquals(latestBTCUSDTTimeStamp, btcUsdt.getTimeStamp());

        Optional<CryptoPrice> ethUsdtRes = res.stream()
                .filter(data -> ETHUSDT.equalsIgnoreCase(data.getCryptoPair()))
                .findFirst();

        assertTrue(ethUsdtRes.isPresent());

        CryptoPrice ethUsdt = ethUsdtRes.get();
        assertNotNull(ethUsdt);
        assertEquals(maxETHUSDTBidPrice, ethUsdt.getBidPrice());
        assertEquals(maxETHUSDTAskPrice, ethUsdt.getAskPrice());
        assertEquals(latestETHUSDTTimeStamp, ethUsdt.getTimeStamp());
    }

}
