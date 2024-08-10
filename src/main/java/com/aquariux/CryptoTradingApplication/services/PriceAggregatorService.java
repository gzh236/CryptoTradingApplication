package com.aquariux.CryptoTradingApplication.services;

import com.aquariux.CryptoTradingApplication.constants.TradeActions;
import com.aquariux.CryptoTradingApplication.entities.CryptoPrice;
import com.aquariux.CryptoTradingApplication.models.records.BinancePricingData;
import com.aquariux.CryptoTradingApplication.models.records.HuobiPricingData;
import com.aquariux.CryptoTradingApplication.models.records.PricingData;
import com.aquariux.CryptoTradingApplication.repositories.CryptoPriceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.*;

@Service
@Slf4j
public class PriceAggregatorService {

    private final CryptoPriceRepository repository;

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public PriceAggregatorService(CryptoPriceRepository repository, WebClient.Builder webClientBuilder) {
        this.repository = repository;
        this.webClientBuilder = webClientBuilder;
    }

    @Scheduled(fixedRate = 10000)
    public void fetchAndStorePricingData() {
        // fetch data from APIs
        Flux<BinancePricingData> binanceData = fetchPricingData(BINANCE_API, BinancePricingData.class);
        Flux<HuobiPricingData> huobiData = fetchPricingData(HUOBI_API, HuobiPricingData.class);

        log.debug("Binance Data: {}", binanceData);
        log.debug("Huobi Data: {}", huobiData);

        // finding best bid & ask price
        Flux.merge(binanceData, huobiData)
                .collectMultimap(PricingData::getTicker)
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .map(entry -> {
                    String ticker = entry.getKey();
                    // explicit cast to PricingData needed
                    List<PricingData> pricingData = entry.getValue().stream()
                            .map(PricingData.class::cast)
                            .toList();

                    BigDecimal bestBidPrice = aggregateBestPrice(pricingData, TradeActions.SELL).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal bestAskingPrice = aggregateBestPrice(pricingData, TradeActions.BUY).setScale(2, RoundingMode.HALF_UP);

                    return createCryptoPriceEntity(ticker, bestBidPrice, bestAskingPrice);
                })
                .flatMap(entity -> Mono.fromCallable(() -> repository.save(entity)))
                .subscribe();
    }


    private <T extends PricingData> Flux<T> fetchPricingData(String uri, Class<T> clazz) {
        log.debug("Retrieving data from: {}", uri);

        return webClientBuilder.build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(clazz)
                .onErrorResume(e -> {
                    log.error("Failed to retrieve data from {}: {}",uri, e.getMessage());
                    return Mono.empty();
                })
                .filter(data -> BTCUSDT.equalsIgnoreCase(data.getTicker()) || ETHUSDT.equalsIgnoreCase(data.getTicker()));
    }

    private BigDecimal aggregateBestPrice(List<PricingData> pricingData, TradeActions action) {
        log.debug("Getting best price for action: {}", action.name());

        if (action.equals(TradeActions.SELL)) {
            return pricingData.stream()
                    .map(PricingData::getBidPrice)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        } else {
            return pricingData.stream()
                    .map(PricingData::getAskPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        }
    }

    private CryptoPrice createCryptoPriceEntity(String ticker, BigDecimal bidPrice, BigDecimal askingPrice) {
        return CryptoPrice.builder()
                .cryptoPair(ticker)
                .bidPrice(bidPrice)
                .askPrice(askingPrice)
                .timeStamp(LocalDateTime.now())
                .build();
    }

}
