package com.aquariux.CryptoTradingApplication.mappers;

import com.aquariux.CryptoTradingApplication.entities.Transaction;
import com.aquariux.CryptoTradingApplication.models.TransactionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransactionMapper {

    public static TransactionModel mapToModel(Transaction entity) {
        if (Objects.isNull(entity))
            return null;

        return TransactionModel.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .cryptoPair(entity.getCryptoPair())
                .price(entity.getPrice())
                .tradeAction(entity.getTradeAction())
                .timeStamp(entity.getTimeStamp())
                .build();
    }

    public static List<TransactionModel> mapToModelList(List<Transaction> entityList) {
        if (Objects.isNull(entityList) || entityList.isEmpty())
            return null;

        List<TransactionModel> modelList = new ArrayList<>();
        for (Transaction entity : entityList) {
            modelList.add(mapToModel(entity));
        }

        return modelList;
    }

    public static Transaction mapToEntity(TransactionModel model) {
        if (Objects.isNull(model))
            return null;

        return Transaction.builder()
                .id(model.getId())
                .amount(model.getAmount())
                .cryptoPair(model.getCryptoPair())
                .price(model.getPrice())
                .tradeAction(model.getTradeAction())
                .timeStamp(model.getTimeStamp())
                .build();
    }

    public static List<Transaction> mapToEntityList(List<TransactionModel> modelList) {
        if (Objects.isNull(modelList) || modelList.isEmpty())
            return null;

        List<Transaction> entityList = new ArrayList<>();
        for (TransactionModel model : modelList) {
            entityList.add(mapToEntity(model));
        }

        return entityList;
    }

}
