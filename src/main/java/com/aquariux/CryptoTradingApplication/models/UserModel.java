package com.aquariux.CryptoTradingApplication.models;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Builder
public class UserModel {

    private Long id;

    private String username;

    @ToString.Exclude
    private List<WalletModel> wallets;

    @ToString.Exclude
    private List<TransactionModel> transactions;

}
