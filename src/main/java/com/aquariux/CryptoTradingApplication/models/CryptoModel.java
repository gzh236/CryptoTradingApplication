package com.aquariux.CryptoTradingApplication.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CryptoModel {

    private Long id;

    private String ticker; // crypto pair might be a better name?

    private String descriptiveName;

    private BigDecimal bidPrice;

    private BigDecimal askPrice;


}
