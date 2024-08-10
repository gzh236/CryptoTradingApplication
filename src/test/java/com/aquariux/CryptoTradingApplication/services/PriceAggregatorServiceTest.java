package com.aquariux.CryptoTradingApplication.services;

import com.aquariux.CryptoTradingApplication.entities.CryptoPrice;
import com.aquariux.CryptoTradingApplication.models.records.BinancePricingData;
import com.aquariux.CryptoTradingApplication.models.records.HuobiPricingData;
import com.aquariux.CryptoTradingApplication.repositories.CryptoPriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceAggregatorServiceTest {

    @Mock
    CryptoPriceRepository repository;

    @Mock
    WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    PriceAggregatorService service;

    @BeforeEach
    void setup() {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void testWebBuilderSetup() {
        webClient = webClientBuilder.build();

        assertNotNull(webClient);
        assertNotNull(webClient.get());
        assertEquals(requestHeadersUriSpec, webClient.get());
        assertNotNull(requestHeadersUriSpec.uri(BINANCE_API));
        assertNotNull(requestHeadersSpec);
        assertNotNull(requestHeadersSpec.retrieve());
    }

    @Test
    void shouldFetchAndStorePricingData() {
        webClient = webClientBuilder.build();

        BinancePricingData mockBinanceBTCUSDTData = new BinancePricingData(BTCUSDT, new BigDecimal("60596.89000000"), new BigDecimal("60596.90000000"));
        BinancePricingData mockBinanceETHUSDTData = new BinancePricingData(ETHUSDT, new BigDecimal("2601.21000000"), new BigDecimal("2601.22000000"));
        HuobiPricingData mockHuobiBTCUSDTData = new HuobiPricingData(BTCUSDT, new BigDecimal("60363.12"), new BigDecimal("60363.13"));
        HuobiPricingData mockHuobiETHUSDTData = new HuobiPricingData(ETHUSDT, new BigDecimal("2601.5"), new BigDecimal("2601.51"));

        when(responseSpec.bodyToFlux(BinancePricingData.class))
                .thenReturn(Flux.just(mockBinanceBTCUSDTData, mockBinanceETHUSDTData));
        when(responseSpec.bodyToFlux(HuobiPricingData.class))
                .thenReturn(Flux.just(mockHuobiBTCUSDTData, mockHuobiETHUSDTData));

        when(repository.save(any(CryptoPrice.class))).thenAnswer(invoc -> invoc.getArgument(0));

        service.fetchAndStorePricingData();

        verify(repository, times(2)).save(any(CryptoPrice.class));
    }

}