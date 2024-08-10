package com.aquariux.CryptoTradingApplication.mappers;

import com.aquariux.CryptoTradingApplication.entities.Wallet;
import com.aquariux.CryptoTradingApplication.models.WalletModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WalletMapper {

    public static WalletModel mapToModel(Wallet entity) {
        if (Objects.isNull(entity))
            return null;

        return WalletModel.builder()
                .id(entity.getId())
                .balance(entity.getAmount())
                .build();
    }

    public static List<WalletModel> mapToModelList(List<Wallet> entityList) {
        List<WalletModel> modelList = new ArrayList<>();
        for (Wallet entity : entityList) {
            modelList.add(mapToModel(entity));
        }

        return modelList;
    }

    public static Wallet mapToEntity(WalletModel model) {
        if (Objects.isNull(model))
            return null;

        return Wallet.builder()
                .id(model.getId())
                .crypto(model.getCrypto())
                .amount(model.getBalance())
                .build();
    }

    public static List<Wallet> mapToEntityList(List<WalletModel> modelList) {
        if (Objects.isNull(modelList) || modelList.isEmpty())
            return null;

        List<Wallet> entityList = new ArrayList<>();
        for (WalletModel model : modelList) {
            entityList.add(mapToEntity(model));
        }

        return entityList;
    }

}
