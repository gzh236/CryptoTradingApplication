package com.aquariux.CryptoTradingApplication.mappers;

import com.aquariux.CryptoTradingApplication.entities.CryptoPrice;
import com.aquariux.CryptoTradingApplication.models.CryptoPriceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CryptoPriceMapper {

    public static CryptoPriceModel convertToModel(CryptoPrice entity) {
        if (Objects.isNull(entity))
            return null;

        return CryptoPriceModel.builder()
                .id(entity.getId())
                .cryptoPair(entity.getCryptoPair())
                .bidPrice(entity.getBidPrice())
                .askPrice(entity.getAskPrice())
                .timeStamp(entity.getTimeStamp())
                .build();
    }

    public static List<CryptoPriceModel> convertToModelList(List<CryptoPrice> entityList) {
        if (Objects.isNull(entityList) || entityList.isEmpty())
            return null;

        List<CryptoPriceModel> modelList = new ArrayList<>();

        for (CryptoPrice entity : entityList) {
            modelList.add(convertToModel(entity));
        }

        return modelList;
    }

}
