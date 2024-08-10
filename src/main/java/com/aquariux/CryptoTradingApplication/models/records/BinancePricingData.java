package com.aquariux.CryptoTradingApplication.models.records;

import java.math.BigDecimal;

public record BinancePricingData(String ticker, BigDecimal bidPrice, BigDecimal askPrice) implements PricingData {

    @Override
    public String getTicker() {
        return ticker;
    }

    @Override
    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    @Override
    public BigDecimal getAskPrice() {
        return askPrice;
    }

}

