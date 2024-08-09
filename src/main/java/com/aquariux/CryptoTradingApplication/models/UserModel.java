package com.aquariux.CryptoTradingApplication.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class UserModel {

    private Long id;

    private String username;

    private BigDecimal walletBalance;

    private List<WalletModel> wallets;

}
