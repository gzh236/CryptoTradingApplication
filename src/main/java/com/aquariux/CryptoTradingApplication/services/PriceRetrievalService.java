package com.aquariux.CryptoTradingApplication.services;

import com.aquariux.CryptoTradingApplication.entities.CryptoPrice;
import com.aquariux.CryptoTradingApplication.exceptions.TechnicalErrorException;
import com.aquariux.CryptoTradingApplication.exceptions.TickerNotFoundException;
import com.aquariux.CryptoTradingApplication.models.CryptoPriceModel;
import com.aquariux.CryptoTradingApplication.repositories.CryptoPriceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.aquariux.CryptoTradingApplication.mappers.CryptoPriceMapper.convertToModel;
import static com.aquariux.CryptoTradingApplication.mappers.CryptoPriceMapper.convertToModelList;

@Service
@Slf4j
public class PriceRetrievalService {

    private final CryptoPriceRepository repository;

    @Autowired
    public PriceRetrievalService(CryptoPriceRepository repository) {
        this.repository = repository;
    }

    public CryptoPriceModel getLatestPriceForTicker(String ticker) throws TickerNotFoundException {
        if (Objects.isNull(ticker) || ticker.isEmpty()) {
            log.error("Ticker not provided when trying to retrieve latest prices");
            throw new TickerNotFoundException("Ticker not provided");
        }

        log.info("Retrieving latest prices for ticker: {}", ticker);
        Optional<CryptoPrice> optional = repository.findLatestPriceByTicker(ticker);

        if (optional.isEmpty()) {
            log.error("No prices for Ticker: {} not found!", ticker);
            throw new TickerNotFoundException("No prices for Ticker: " + ticker + " found!");
        }

        CryptoPrice entity = optional.get();
        return convertToModel(entity);
    }

    public List<CryptoPriceModel> getLatestPricesForAllTickers() throws TechnicalErrorException {
        log.info("Retrieving latest prices for all tickers");
        Optional<List<CryptoPrice>> optional = repository.findLatestPriceForAllTickers();

        if (optional.isEmpty()) {
            log.error("No prices found in database!");
            throw new TechnicalErrorException("No prices found in database!");
        }

        return convertToModelList(optional.get());
    }

}
