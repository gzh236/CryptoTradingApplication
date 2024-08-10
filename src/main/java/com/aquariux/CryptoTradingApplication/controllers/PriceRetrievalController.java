package com.aquariux.CryptoTradingApplication.controllers;

import com.aquariux.CryptoTradingApplication.exceptions.TechnicalErrorException;
import com.aquariux.CryptoTradingApplication.exceptions.TickerNotFoundException;
import com.aquariux.CryptoTradingApplication.models.CryptoPriceModel;
import com.aquariux.CryptoTradingApplication.services.PriceRetrievalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/prices")
@Slf4j
public class PriceRetrievalController {

    private final PriceRetrievalService service;

    @Autowired
    public PriceRetrievalController(PriceRetrievalService service) {
        this.service = service;
    }

    @GetMapping(value = "/latest", params = "ticker")
    public ResponseEntity<CryptoPriceModel> getLatestPriceForTicker(@RequestParam String ticker) {
        log.info("Retrieving latest prices for ticker: {}", ticker);
        try {
             return ResponseEntity.ok(service.getLatestPriceForTicker(ticker));
        } catch (TickerNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/latest/all")
    public ResponseEntity<List<CryptoPriceModel>> getLatestPricesForAllTickers() {
        log.info("Retrieving latest prices for all tickers");
        try {
            return ResponseEntity.ok(service.getLatestPricesForAllTickers());
        } catch (TechnicalErrorException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

}
