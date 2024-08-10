package com.aquariux.CryptoTradingApplication.services;

import com.aquariux.CryptoTradingApplication.constants.TradeActions;
import com.aquariux.CryptoTradingApplication.entities.Transaction;
import com.aquariux.CryptoTradingApplication.exceptions.InsufficientFundsException;
import com.aquariux.CryptoTradingApplication.exceptions.TechnicalErrorException;
import com.aquariux.CryptoTradingApplication.exceptions.TickerNotFoundException;
import com.aquariux.CryptoTradingApplication.exceptions.WalletNotFoundException;
import com.aquariux.CryptoTradingApplication.models.CryptoPriceModel;
import com.aquariux.CryptoTradingApplication.models.TransactionModel;
import com.aquariux.CryptoTradingApplication.models.UserModel;
import com.aquariux.CryptoTradingApplication.models.WalletModel;
import com.aquariux.CryptoTradingApplication.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private PriceRetrievalService priceService;

    @Mock
    private WalletService walletService;

    @Mock
    private UserService userService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private UserModel mockUser;
    private WalletModel usdtWallet;
    private WalletModel cryptoWallet;
    private CryptoPriceModel mockPrice;

    @BeforeEach
    void setUp() {
        mockUser = UserModel.builder().id(1L).username("testUser").build();
        List<WalletModel> wallets = new ArrayList<>();
        WalletModel model = (WalletModel.builder()
                .user(mockUser)
                .balance(new BigDecimal(INITIAL_USDT_BALANCE).setScale(2, RoundingMode.HALF_UP))
                .crypto(USDT)
                .build());
        wallets.add(model);
        mockUser.setWallets(wallets);
        usdtWallet = WalletModel.builder().id(1L).crypto(USDT).balance(new BigDecimal("10000").setScale(2, RoundingMode.HALF_UP)).user(mockUser).build();
        cryptoWallet = WalletModel.builder().id(2L).crypto("BTCUSDT").balance(new BigDecimal("2").setScale(2, RoundingMode.HALF_UP)).user(mockUser).build();
        mockPrice = CryptoPriceModel.builder().cryptoPair("BTCUSDT").askPrice(new BigDecimal("50000").setScale(2, RoundingMode.HALF_UP)).bidPrice(new BigDecimal("49000").setScale(2, RoundingMode.HALF_UP)).build();
    }

    @Test
    void shouldExecuteBuyTransactionSuccessfully() throws Exception {
        when(priceService.getLatestPriceForTicker(anyString())).thenReturn(mockPrice);
        when(userService.getUserById(anyLong())).thenReturn(mockUser);
        when(walletService.retrieveWalletByUserIdAndCrypto(anyLong(), eq(USDT))).thenReturn(usdtWallet);
        when(walletService.retrieveWalletByUserIdAndCrypto(anyLong(), eq(BTCUSDT))).thenReturn(cryptoWallet);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionModel transaction = transactionService.executeTransaction(1L, BTCUSDT, new BigDecimal("0.1").setScale(2, RoundingMode.HALF_UP), TradeActions.BUY);

        assertNotNull(transaction);
        assertEquals(BTCUSDT, transaction.getCryptoPair());
        assertEquals(new BigDecimal("0.1").setScale(2, RoundingMode.HALF_UP), transaction.getAmount());

        BigDecimal expectedUSDTDecrease = new BigDecimal("5000.00").setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedCryptoIncrease = new BigDecimal("0.1").setScale(2, RoundingMode.HALF_UP);

        verify(walletService, times(1)).decreaseWalletBalance(eq(usdtWallet), eq(expectedUSDTDecrease));
        verify(walletService, times(1)).increaseWalletBalance(eq(cryptoWallet), eq(expectedCryptoIncrease));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }


    @Test
    void shouldExecuteSellTransactionSuccessfully() throws Exception {
        when(priceService.getLatestPriceForTicker(anyString())).thenReturn(mockPrice);
        when(userService.getUserById(anyLong())).thenReturn(mockUser);
        when(walletService.retrieveWalletByUserIdAndCrypto(anyLong(), eq(BTCUSDT))).thenReturn(cryptoWallet);
        when(walletService.retrieveWalletByUserIdAndCrypto(anyLong(), eq(USDT))).thenReturn(usdtWallet);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionModel transaction = transactionService.executeTransaction(1L, BTCUSDT, new BigDecimal("0.10").setScale(2, RoundingMode.HALF_UP), TradeActions.SELL);

        assertNotNull(transaction);
        assertEquals("BTCUSDT", transaction.getCryptoPair());
        assertEquals(new BigDecimal("0.10"), transaction.getAmount());
        verify(walletService, times(1)).decreaseWalletBalance(cryptoWallet, new BigDecimal("0.10").setScale(2, RoundingMode.HALF_UP));
        verify(walletService, times(1)).increaseWalletBalance(usdtWallet, new BigDecimal("4900.00").setScale(2, RoundingMode.HALF_UP));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void shouldThrowWalletNotFoundExceptionWhenSellingWithNoCryptoWallet() throws TickerNotFoundException, WalletNotFoundException {
        when(priceService.getLatestPriceForTicker(anyString())).thenReturn(mockPrice);
        when(userService.getUserById(anyLong())).thenReturn(mockUser);
        when(walletService.retrieveWalletByUserIdAndCrypto(anyLong(), eq("BTCUSDT"))).thenReturn(null);

        assertThrows(TechnicalErrorException.class, () -> {
            transactionService.executeTransaction(1L, "BTCUSDT", new BigDecimal("0.1"), TradeActions.SELL);
        });

    }

    @Test
    void shouldThrowInsufficientFundsExceptionWhenBuyingWithNotEnoughUsdt() throws TickerNotFoundException, WalletNotFoundException, InsufficientFundsException {
        when(priceService.getLatestPriceForTicker(anyString())).thenReturn(mockPrice);
        when(userService.getUserById(anyLong())).thenReturn(mockUser);
        when(walletService.retrieveWalletByUserIdAndCrypto(anyLong(), eq(USDT)))
                .thenReturn(usdtWallet);

        doThrow(new InsufficientFundsException("Insufficient funds"))
                .when(walletService).decreaseWalletBalance(any(WalletModel.class), any(BigDecimal.class));

        assertThrows(TechnicalErrorException.class, () -> {
            transactionService.executeTransaction(1L, "BTCUSDT", new BigDecimal("1000"), TradeActions.BUY);
        });
    }

    @Test
    void shouldThrowTickerNotFoundExceptionWhenNoPriceFound() throws TickerNotFoundException {
        when(priceService.getLatestPriceForTicker(anyString())).thenThrow(new TickerNotFoundException("Ticker not found"));

        assertThrows(TechnicalErrorException.class, () -> {
            transactionService.executeTransaction(1L, "BTCUSDT", new BigDecimal("0.1"), TradeActions.BUY);
        });

    }
}
