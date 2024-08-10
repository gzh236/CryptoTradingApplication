package com.aquariux.CryptoTradingApplication.controllers;

import com.aquariux.CryptoTradingApplication.exceptions.WalletNotFoundException;
import com.aquariux.CryptoTradingApplication.models.WalletModel;
import com.aquariux.CryptoTradingApplication.services.WalletService;
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

import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.USDT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService service;

    @InjectMocks
    private WalletController controller;

    private WalletModel walletModel1;
    private WalletModel walletModel2;

    @BeforeEach
    void setUp() {
        walletModel1 = WalletModel.builder()
                .id(1L)
                .crypto(USDT)
                .balance(new BigDecimal("10000.00"))
                .build();

        walletModel2 = WalletModel.builder()
                .id(2L)
                .crypto("BTCUSDT")
                .balance(new BigDecimal("0.5"))
                .build();
    }

    @Test
    void shouldReturnUserWalletsSuccessfully() throws WalletNotFoundException {
        List<WalletModel> wallets = Arrays.asList(walletModel1, walletModel2);
        when(service.retrieveWalletsByUserId(1L)).thenReturn(wallets);

        ResponseEntity<List<WalletModel>> response = controller.getUserWallets(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(wallets, response.getBody());
        verify(service, times(1)).retrieveWalletsByUserId(1L);
    }

    @Test
    void shouldReturnNotFoundWhenNoWalletsFound() throws WalletNotFoundException {
        when(service.retrieveWalletsByUserId(1L)).thenThrow(new WalletNotFoundException("No wallets found for user ID: 1"));

        ResponseEntity<List<WalletModel>> response = controller.getUserWallets(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(service, times(1)).retrieveWalletsByUserId(1L);
    }
}
