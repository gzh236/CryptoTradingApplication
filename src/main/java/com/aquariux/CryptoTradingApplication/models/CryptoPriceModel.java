package com.aquariux.CryptoTradingApplication.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CryptoPriceModel {

    private Long id;

    private String cryptoPair;

    private BigDecimal bidPrice;

    private BigDecimal askPrice;

    private LocalDateTime timeStamp;

}
