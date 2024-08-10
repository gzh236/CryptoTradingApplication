package com.aquariux.CryptoTradingApplication.mappers;

import com.aquariux.CryptoTradingApplication.entities.User;
import com.aquariux.CryptoTradingApplication.models.UserModel;

import java.util.Objects;

public class UserMapper {

    public static UserModel mapToModel(User entity) {
        if (Objects.isNull(entity))
            return null;

        return UserModel.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .wallets(WalletMapper.mapToModelList(entity.getWallets()))
                .transactions(TransactionMapper.mapToModelList(entity.getTransactions()))
                .build();
    }

    public static User mapToEntity(UserModel model) {
        if (Objects.isNull(model))
            return null;

        return User.builder()
                .id(model.getId())
                .username(model.getUsername())
                .wallets(WalletMapper.mapToEntityList(model.getWallets()))
                .transactions(TransactionMapper.mapToEntityList(model.getTransactions()))
                .build();
    }

    public static UserModel mapUserToModelWithoutWallets(User entity) {
        if (Objects.isNull(entity)) {
            return null;
        }

        return UserModel.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .build();
    }

    public static User mapUserToEntityWithoutWallets(UserModel model) {
        if (Objects.isNull(model)) {
            return null;
        }

        return User.builder()
                .id(model.getId())
                .username(model.getUsername())
                .build();
    }

}
