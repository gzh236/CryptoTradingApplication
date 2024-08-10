package com.aquariux.CryptoTradingApplication.mappers;

import com.aquariux.CryptoTradingApplication.entities.User;
import com.aquariux.CryptoTradingApplication.entities.Wallet;
import com.aquariux.CryptoTradingApplication.models.UserModel;
import com.aquariux.CryptoTradingApplication.models.WalletModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.aquariux.CryptoTradingApplication.mappers.UserMapper.mapUserToEntityWithoutWallets;
import static com.aquariux.CryptoTradingApplication.mappers.UserMapper.mapUserToModelWithoutWallets;

public class WalletMapper {

    public static WalletModel mapToModel(Wallet entity) {
        if (Objects.isNull(entity))
            return null;

        UserModel userModel = mapUserToModelWithoutWallets(entity.getUser());

        return WalletModel.builder()
                .id(entity.getId())
                .user(userModel)
                .crypto(entity.getCrypto())
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

        User userEntity = mapUserToEntityWithoutWallets(model.getUser());

        return Wallet.builder()
                .id(model.getId())
                .user(userEntity)
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
