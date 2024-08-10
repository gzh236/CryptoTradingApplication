package com.aquariux.CryptoTradingApplication.controllers;

import com.aquariux.CryptoTradingApplication.exceptions.TechnicalErrorException;
import com.aquariux.CryptoTradingApplication.exceptions.TickerNotFoundException;
import com.aquariux.CryptoTradingApplication.models.CryptoPriceModel;
import com.aquariux.CryptoTradingApplication.services.PriceRetrievalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.BTCUSDT;
import static com.aquariux.CryptoTradingApplication.testdata.CryptoPriceTestData.getCryptoPriceModel;
import static com.aquariux.CryptoTradingApplication.testdata.CryptoPriceTestData.getCryptoPriceModelList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PriceRetrievalControllerTests {

    @Mock
    PriceRetrievalService service;

    @InjectMocks
    PriceRetrievalController controller;

    @Test
    void shouldGetLatestPriceForTicker() throws TickerNotFoundException {
        CryptoPriceModel model = getCryptoPriceModel(1L, BTCUSDT);
        when(service.getLatestPriceForTicker(anyString())).thenReturn(model);

        ResponseEntity<CryptoPriceModel> response = controller.getLatestPriceForTicker(BTCUSDT);

        verify(service, times(1)).getLatestPriceForTicker(BTCUSDT);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BTCUSDT", response.getBody().getCryptoPair());
        assertEquals(new BigDecimal("60001.23"), response.getBody().getBidPrice());
        assertEquals(new BigDecimal("60002.01"), response.getBody().getAskPrice());
    }

    @Test
    void shouldGetLatestPriceForAllTickers() throws TechnicalErrorException {
        List<CryptoPriceModel> models = getCryptoPriceModelList(2);
        when(service.getLatestPricesForAllTickers()).thenReturn(models);

        ResponseEntity<List<CryptoPriceModel>> response = controller.getLatestPricesForAllTickers();

        verify(service, times(1)).getLatestPricesForAllTickers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

}
