package com.aquariux.CryptoTradingApplication.controllers;

import com.aquariux.CryptoTradingApplication.constants.TradeActions;
import com.aquariux.CryptoTradingApplication.exceptions.TechnicalErrorException;
import com.aquariux.CryptoTradingApplication.models.TradeRequestModel;
import com.aquariux.CryptoTradingApplication.models.TransactionModel;
import com.aquariux.CryptoTradingApplication.models.UserModel;
import com.aquariux.CryptoTradingApplication.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService service;

    @InjectMocks
    private TransactionController controller;

    private TradeRequestModel tradeRequestModel;
    private TransactionModel transactionModel;
    private UserModel userModel;

    @BeforeEach
    void setUp() {
        userModel = UserModel.builder().id(1L).username("testUser").build();
        tradeRequestModel = new TradeRequestModel(1L, "BTCUSDT", new BigDecimal("0.1"), TradeActions.BUY);
        transactionModel = TransactionModel.builder()
                .id(1L)
                .user(userModel)
                .cryptoPair("BTCUSDT")
                .amount(new BigDecimal("0.1"))
                .tradeAction(TradeActions.BUY)
                .build();
    }

    @Test
    void shouldMakeTradeActionSuccessfully() throws TechnicalErrorException {
        // Arrange
        when(service.executeTransaction(anyLong(), anyString(), any(BigDecimal.class), any(TradeActions.class)))
                .thenReturn(transactionModel);

        // Act
        ResponseEntity<TransactionModel> response = controller.makeTradeAction(tradeRequestModel);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactionModel, response.getBody());
        verify(service, times(1)).executeTransaction(anyLong(), anyString(), any(BigDecimal.class), any(TradeActions.class));
    }

    @Test
    void shouldReturnInternalServerErrorWhenTradeFails() throws TechnicalErrorException {
        // Arrange
        when(service.executeTransaction(anyLong(), anyString(), any(BigDecimal.class), any(TradeActions.class)))
                .thenThrow(new TechnicalErrorException("Trade failed"));

        // Act
        ResponseEntity<TransactionModel> response = controller.makeTradeAction(tradeRequestModel);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(service, times(1)).executeTransaction(anyLong(), anyString(), any(BigDecimal.class), any(TradeActions.class));
    }

    @Test
    void shouldReturnUserTransactionHistory() {
        // Arrange
        TransactionModel transaction1 = TransactionModel.builder().id(1L).user(userModel).cryptoPair("BTCUSDT").amount(new BigDecimal("0.1")).tradeAction(TradeActions.BUY).build();
        TransactionModel transaction2 = TransactionModel.builder().id(2L).user(userModel).cryptoPair("ETHUSDT").amount(new BigDecimal("1.0")).tradeAction(TradeActions.SELL).build();
        List<TransactionModel> transactionHistory = Arrays.asList(transaction1, transaction2);

        when(service.retrieveUserTransactionHistory(anyLong())).thenReturn(transactionHistory);

        // Act
        ResponseEntity<List<TransactionModel>> response = controller.getUserTransactionHistory(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactionHistory, response.getBody());
        verify(service, times(1)).retrieveUserTransactionHistory(anyLong());
    }

    @Test
    void shouldReturnNotFoundWhenNoTransactionHistoryFound() {
        // Arrange
        when(service.retrieveUserTransactionHistory(anyLong())).thenReturn(null);

        // Act
        ResponseEntity<List<TransactionModel>> response = controller.getUserTransactionHistory(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(service, times(1)).retrieveUserTransactionHistory(anyLong());
    }
}
