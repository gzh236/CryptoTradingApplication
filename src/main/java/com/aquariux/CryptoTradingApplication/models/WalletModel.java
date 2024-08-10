package com.aquariux.CryptoTradingApplication.models;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@Builder
@ToString(exclude = "user")
public class WalletModel {

    @Id
    private Long id;

    private String crypto;

    private BigDecimal balance;

    private UserModel user;

}
