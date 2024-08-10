package com.aquariux.CryptoTradingApplication.mappers;

import com.aquariux.CryptoTradingApplication.entities.CryptoPrice;
import com.aquariux.CryptoTradingApplication.models.CryptoPriceModel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.aquariux.CryptoTradingApplication.mappers.CryptoPriceMapper.convertToModel;
import static com.aquariux.CryptoTradingApplication.mappers.CryptoPriceMapper.convertToModelList;
import static com.aquariux.CryptoTradingApplication.testdata.CryptoPriceTestData.getMockCryptoPrice;
import static com.aquariux.CryptoTradingApplication.testdata.CryptoPriceTestData.getMockCryptoPriceList;
import static org.junit.jupiter.api.Assertions.*;


class CryptoPriceMapperTest {

    @Test
    void shouldConvertToModel() {
        CryptoPrice entity = getMockCryptoPrice();
        CryptoPriceModel res = convertToModel(entity);

        assertNotNull(res);
        assertEquals(entity.getId(), res.getId());
        assertEquals(entity.getCryptoPair(), res.getCryptoPair());
        assertEquals(entity.getBidPrice(), res.getBidPrice());
        assertEquals(entity.getAskPrice(), res.getAskPrice());
        assertEquals(entity.getTimeStamp(), res.getTimeStamp());
    }

    @Test
    void shouldReturnNull() {
        CryptoPriceModel res = convertToModel(null);
        assertNull(res);
    }

    @Test
    void shouldConvertToModelList() {
        List<CryptoPrice> entityList = getMockCryptoPriceList(4);
        List<CryptoPriceModel> res = convertToModelList(entityList);

        assertNotNull(res);
        assertEquals(entityList.size(), res.size());

        for (int i = 0; i < entityList.size(); i++) {
            assertEquals(entityList.get(i).getId(), res.get(i).getId());
            assertEquals(entityList.get(i).getCryptoPair(), res.get(i).getCryptoPair());
            assertEquals(entityList.get(i).getBidPrice(), res.get(i).getBidPrice());
            assertEquals(entityList.get(i).getAskPrice(), res.get(i).getAskPrice());
            assertEquals(entityList.get(i).getTimeStamp(), res.get(i).getTimeStamp());
        }
    }

    @Test
    void shouldReturnNulIfListIsEmpty() {
        List<CryptoPriceModel> res = convertToModelList(new ArrayList<>());
        assertNull(res);
    }

    @Test
    void shouldReturnNullIfListIsNull() {
        List<CryptoPriceModel> res = convertToModelList(null);
        assertNull(res);
    }

}