package com.aquariux.CryptoTradingApplication.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CryptoPriceModel {

    private Long id;

    private String cryptoPair;

    private String descriptiveName;

    private BigDecimal bidPrice;

    private BigDecimal askPrice;


}
