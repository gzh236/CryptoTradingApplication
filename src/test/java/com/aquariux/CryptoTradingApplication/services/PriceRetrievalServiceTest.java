package com.aquariux.CryptoTradingApplication.services;

import com.aquariux.CryptoTradingApplication.entities.CryptoPrice;
import com.aquariux.CryptoTradingApplication.exceptions.TechnicalErrorException;
import com.aquariux.CryptoTradingApplication.exceptions.TickerNotFoundException;
import com.aquariux.CryptoTradingApplication.models.CryptoPriceModel;
import com.aquariux.CryptoTradingApplication.repositories.CryptoPriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.BTCUSDT;
import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.ETHUSDT;
import static com.aquariux.CryptoTradingApplication.testdata.CryptoPriceTestData.getMockCryptoPrice;
import static com.aquariux.CryptoTradingApplication.testdata.CryptoPriceTestData.getMockCryptoPriceList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceRetrievalServiceTest {

    @Mock
    CryptoPriceRepository repository;

    @InjectMocks
    PriceRetrievalService service;


    @Test
    void shouldGetLatestPriceForTicker() throws TickerNotFoundException {
        Optional<CryptoPrice> optional = Optional.of(getMockCryptoPrice());
        CryptoPrice entity = optional.orElse(null);

        when(repository.findLatestPriceByTicker(anyString())).thenReturn(optional);

        CryptoPriceModel model = service.getLatestPriceForTicker(BTCUSDT);

        assertNotNull(model);
        assertNotNull(model.getId());
        assertNotNull(model.getCryptoPair());
        assertNotNull(model.getBidPrice());
        assertNotNull(model.getAskPrice());
        assertNotNull(model.getTimeStamp());

        assertEquals(entity.getId(), model.getId());
        assertEquals(entity.getCryptoPair(), model.getCryptoPair());
        assertEquals(entity.getBidPrice(), model.getBidPrice());
        assertEquals(entity.getAskPrice(), model.getAskPrice());
        assertEquals(entity.getTimeStamp(), model.getTimeStamp());
    }

    @Test
    void shouldThrowTickerNotFoundExceptionIfTickerIsEmpty() {
        assertThrows(TickerNotFoundException.class, () -> service.getLatestPriceForTicker(""));
    }

    @Test
    void shouldThrowTickerNotFoundExceptionIfTickerIsNull() {
        assertThrows(TickerNotFoundException.class, () -> service.getLatestPriceForTicker(null));
    }

    @Test
    void shouldThrowTickerNotFoundExceptionIfTickerIsInvalid() {
        when(repository.findLatestPriceByTicker(anyString())).thenReturn(Optional.empty());
        assertThrows(TickerNotFoundException.class, () -> service.getLatestPriceForTicker("INVALID"));
    }

    @Test
    void shouldRetrieveLatestPricesForAllTickers() throws TechnicalErrorException {
        List<CryptoPrice> entityList = getMockCryptoPriceList(2);
        when(repository.findLatestPriceForAllTickers()).thenReturn(Optional.of(entityList));

        List<CryptoPriceModel> res = service.getLatestPricesForAllTickers();

        assertNotNull(res);
        assertEquals(entityList.size(), res.size());
        for (int i = 0; i < entityList.size(); i++) {
            assertEquals(entityList.get(i).getId(), res.get(i).getId());
            assertEquals(entityList.get(i).getCryptoPair(), res.get(i).getCryptoPair());
            assertEquals(entityList.get(i).getBidPrice(), res.get(i).getBidPrice());
            assertEquals(entityList.get(i).getAskPrice(), res.get(i).getAskPrice());
            assertEquals(entityList.get(i).getTimeStamp(), res.get(i).getTimeStamp());
        }
    }

    @Test
    void shouldThrowTechnicalErrorExceptionIfNoPricesFound() {
        when(repository.findLatestPriceForAllTickers()).thenReturn(Optional.empty());
        assertThrows(TechnicalErrorException.class, () -> service.getLatestPricesForAllTickers());
    }



}