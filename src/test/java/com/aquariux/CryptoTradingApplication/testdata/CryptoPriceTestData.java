package com.aquariux.CryptoTradingApplication.testdata;

import com.aquariux.CryptoTradingApplication.entities.CryptoPrice;
import com.aquariux.CryptoTradingApplication.models.CryptoPriceModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.BTCUSDT;
import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.ETHUSDT;

public class CryptoPriceTestData {

    public static CryptoPrice getMockCryptoPrice() {
        return CryptoPrice.builder()
                .id(1L)
                .cryptoPair(BTCUSDT)
                .bidPrice(new BigDecimal("61003.23"))
                .askPrice(new BigDecimal("61004.00"))
                .timeStamp(LocalDateTime.now())
                .build();
    }

    public static List<CryptoPrice> getMockCryptoPriceList(int size) {
        List<CryptoPrice> entityList = new ArrayList<>();
        for (int i = 1; i < size; i++) {
            CryptoPrice entity = CryptoPrice.builder()
                    .id((long) i)
                    .cryptoPair(i % 2 == 0 ? BTCUSDT : ETHUSDT)
                    .bidPrice(new BigDecimal(String.valueOf(61003.23 - (double) i)))
                    .askPrice(new BigDecimal(String.valueOf(61004.00 - (double) i)))
                    .timeStamp(LocalDateTime.now().minusMinutes(i))
                    .build();

            entityList.add(entity);
        }

        return entityList;
    }

    public static CryptoPriceModel getCryptoPriceModel(Long id, String ticker) {
        return CryptoPriceModel.builder()
                .id(id)
                .cryptoPair(ticker)
                .bidPrice(new BigDecimal("60001.23"))
                .askPrice(new BigDecimal("60002.01"))
                .timeStamp(LocalDateTime.now())
                .build();
    }

    public static List<CryptoPriceModel> getCryptoPriceModelList(int size) {
        List<CryptoPriceModel> models = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
           models.add(getCryptoPriceModel((long) i, i % 2 == 0 ? BTCUSDT : ETHUSDT));
        }

        return models;
    }

}
