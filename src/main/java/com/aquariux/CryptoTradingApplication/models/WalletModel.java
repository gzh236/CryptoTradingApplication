package com.aquariux.CryptoTradingApplication.models;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WalletModel {

    @Id
    private Long id;

    private String cryptoPair;

    private BigDecimal balance;

    private UserModel user;

}
