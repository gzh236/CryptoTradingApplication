package com.aquariux.CryptoTradingApplication.models;

import com.aquariux.CryptoTradingApplication.constants.TradeActions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TradeRequestModel {

    private Long userId;

    private String ticker;

    private BigDecimal amount;

    private TradeActions action;

}
