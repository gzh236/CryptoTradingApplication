package com.aquariux.CryptoTradingApplication.models.records;

import java.math.BigDecimal;

public interface PricingData {

    String getTicker();

    BigDecimal getBidPrice();

    BigDecimal getAskPrice();

}
