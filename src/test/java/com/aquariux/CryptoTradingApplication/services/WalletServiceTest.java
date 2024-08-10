package com.aquariux.CryptoTradingApplication.services;

import com.aquariux.CryptoTradingApplication.entities.User;
import com.aquariux.CryptoTradingApplication.entities.Wallet;
import com.aquariux.CryptoTradingApplication.exceptions.InsufficientFundsException;
import com.aquariux.CryptoTradingApplication.exceptions.WalletNotFoundException;
import com.aquariux.CryptoTradingApplication.models.WalletModel;
import com.aquariux.CryptoTradingApplication.repositories.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.INITIAL_USDT_BALANCE;
import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.USDT;
import static com.aquariux.CryptoTradingApplication.mappers.WalletMapper.mapToModel;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository repository;

    @InjectMocks
    private WalletService walletService;

    private User mockUser;
    private Wallet mockWallet;
    private WalletModel mockWalletModel;

    @BeforeEach
    void setUp() {
        mockUser = User.builder().id(1L).username("testUser").build();
        mockWallet = Wallet.builder()
                .id(1L)
                .user(mockUser)
                .crypto(USDT)
                .amount(new BigDecimal(INITIAL_USDT_BALANCE).setScale(2, RoundingMode.HALF_UP))
                .build();
        mockWalletModel = mapToModel(mockWallet);
        mockUser.setWallets(List.of(mockWallet));
    }

    @Test
    void shouldRetrieveWalletByUserIdAndCrypto() throws WalletNotFoundException {
        when(repository.findByUserIdAndCrypto(anyLong(), eq(USDT))).thenReturn(Optional.of(mockWallet));

        WalletModel walletModel = walletService.retrieveWalletByUserIdAndCrypto(1L, USDT);

        assertNotNull(walletModel);
        assertEquals(mockWalletModel.getId(), walletModel.getId());
        assertEquals(mockWalletModel.getCrypto(), walletModel.getCrypto());
        assertEquals(mockWalletModel.getBalance(), walletModel.getBalance());

        verify(repository, times(1)).findByUserIdAndCrypto(1L, USDT);
    }

    @Test
    void shouldThrowWalletNotFoundExceptionWhenRetrievingNonExistentWallet() {
        when(repository.findByUserIdAndCrypto(anyLong(), eq(USDT))).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.retrieveWalletByUserIdAndCrypto(1L, USDT));

        verify(repository, times(1)).findByUserIdAndCrypto(1L, USDT);
    }

    @Test
    void shouldIncreaseWalletBalance() throws WalletNotFoundException {
        BigDecimal increaseAmount = new BigDecimal("1000.00").setScale(2, RoundingMode.HALF_UP);

        walletService.increaseWalletBalance(mockWalletModel, increaseAmount);

        assertEquals(new BigDecimal(INITIAL_USDT_BALANCE).add(increaseAmount), mockWalletModel.getBalance());
        verify(repository, times(1)).save(any(Wallet.class));
    }

    @Test
    void shouldThrowWalletNotFoundExceptionWhenIncreasingBalanceOnNullWallet() {
        assertThrows(WalletNotFoundException.class, () -> walletService.increaseWalletBalance(null, new BigDecimal("1000.00")));
    }

    @Test
    void shouldDecreaseWalletBalance() throws InsufficientFundsException, WalletNotFoundException {
        BigDecimal decreaseAmount = new BigDecimal("1000.00");

        walletService.decreaseWalletBalance(mockWalletModel, decreaseAmount);

        assertEquals(new BigDecimal(INITIAL_USDT_BALANCE).subtract(decreaseAmount), mockWalletModel.getBalance());
        verify(repository, times(1)).save(any(Wallet.class));
    }

    @Test
    void shouldThrowInsufficientFundsExceptionWhenDecreasingMoreThanBalance() {
        BigDecimal decreaseAmount = new BigDecimal(INITIAL_USDT_BALANCE).add(new BigDecimal("1000.00"));

        assertThrows(InsufficientFundsException.class, () -> walletService.decreaseWalletBalance(mockWalletModel, decreaseAmount));
    }

    @Test
    void shouldThrowWalletNotFoundExceptionWhenDecreasingBalanceOnNullWallet() {
        assertThrows(WalletNotFoundException.class, () -> walletService.decreaseWalletBalance(null, new BigDecimal("1000.00")));
    }

    @Test
    void shouldCreateUserWallet() {
        when(repository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet newWallet = walletService.createUserWallet(mockUser, "BTC");

        assertNotNull(newWallet);
        assertEquals(mockUser, newWallet.getUser());
        assertEquals("BTC", newWallet.getCrypto());
        assertEquals(BigDecimal.ZERO, newWallet.getAmount());

        verify(repository, times(1)).save(any(Wallet.class));
    }

    @Test
    void shouldCreateInitialUsdtWallet() {
        when(repository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Wallet newWallet = walletService.createInitialUsdtWallet(mockUser);

        assertNotNull(newWallet);
        assertEquals(mockUser, newWallet.getUser());
        assertEquals(USDT, newWallet.getCrypto());
        assertEquals(new BigDecimal(INITIAL_USDT_BALANCE), newWallet.getAmount());

        verify(repository, times(1)).save(any(Wallet.class));
    }

    @Test
    void shouldRetrieveWalletsByUserIdSuccessfully() throws WalletNotFoundException {
        Wallet wallet1 = Wallet.builder()
                .id(1L)
                .user(mockUser)
                .crypto(USDT)
                .amount(new BigDecimal("10000.00"))
                .build();

        Wallet wallet2 = Wallet.builder()
                .id(2L)
                .user(mockUser)
                .crypto("BTCUSDT")
                .amount(new BigDecimal("0.5"))
                .build();

        List<Wallet> wallets = List.of(wallet1, wallet2);
        when(repository.findByUserId(anyLong())).thenReturn(Optional.of(wallets));

        List<WalletModel> result = walletService.retrieveWalletsByUserId(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(USDT, result.get(0).getCrypto());
        assertEquals(new BigDecimal("10000.00"), result.get(0).getBalance());
        verify(repository, times(1)).findByUserId(1L);
    }

    @Test
    void shouldThrowWalletNotFoundExceptionWhenNoWalletsFound() {
        when(repository.findByUserId(anyLong())).thenReturn(Optional.empty());

        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () -> {
            walletService.retrieveWalletsByUserId(1L);
        });

        assertEquals("No wallets found for user ID: 1", exception.getMessage());
        verify(repository, times(1)).findByUserId(1L);
    }
}
