package com.aquariux.CryptoTradingApplication.models;

import com.aquariux.CryptoTradingApplication.constants.TradeActions;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionModel {

    private Long id;

    private UserModel user;

    private String cryptoPair; // i.e. "ETHUSDT", "BTCUSDT"

    private TradeActions tradeAction;

    private BigDecimal amount;

    private BigDecimal price;

    private LocalDateTime timeStamp;

}
