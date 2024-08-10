package com.aquariux.CryptoTradingApplication.models;

import com.aquariux.CryptoTradingApplication.constants.TradeActions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionModel {

    private Long id;

    @ToString.Exclude
    private UserModel user;

    private String cryptoPair; // i.e. "ETHUSDT", "BTCUSDT"

    private TradeActions tradeAction;

    private BigDecimal amount;

    private BigDecimal price;

    private LocalDateTime timeStamp;

}
