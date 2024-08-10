package com.aquariux.CryptoTradingApplication.models.records;

import java.math.BigDecimal;

public record HuobiPricingData(String symbol, BigDecimal bid, BigDecimal ask) implements PricingData {

    @Override
    public String getTicker() {
        return symbol;
    }

    @Override
    public BigDecimal getBidPrice() {
        return bid;
    }

    @Override
    public BigDecimal getAskPrice() {
        return ask;
    }

}
