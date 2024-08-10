package com.aquariux.CryptoTradingApplication.services;

import com.aquariux.CryptoTradingApplication.constants.ApiConstants;
import com.aquariux.CryptoTradingApplication.entities.User;
import com.aquariux.CryptoTradingApplication.entities.Wallet;
import com.aquariux.CryptoTradingApplication.exceptions.TechnicalErrorException;
import com.aquariux.CryptoTradingApplication.exceptions.UsernameNotUniqueException;
import com.aquariux.CryptoTradingApplication.models.UserModel;
import com.aquariux.CryptoTradingApplication.repositories.UserRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private UserService userService;
    private UserModel userModel;
    private User user;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        userModel = UserModel.builder()
                .id(1L)
                .username("testUser")
                .build();

        user = User.builder()
                .id(1L)
                .username("testUser")
                .build();

        wallet = Wallet.builder()
                .user(user)
                .amount(new BigDecimal(INITIAL_USDT_BALANCE).setScale(2, RoundingMode.HALF_UP))
                .crypto(USDT)
                .build();

        user.setWallets(List.of(wallet));
    }

    @Test
    void shouldGetUserById() {
        when(repository.getReferenceById(anyLong())).thenReturn(user);

        UserModel result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(repository, times(1)).getReferenceById(1L);
    }

    @Test
    void shouldRegisterUserSuccessfully() throws TechnicalErrorException, UsernameNotUniqueException {
        when(repository.findOneByUsername(anyString())).thenReturn(Optional.empty());
        when(repository.save(any(User.class))).thenReturn(user);
        when(walletService.createInitialUsdtWallet(any(User.class))).thenReturn(wallet);

        UserModel result = userService.registerUser(userModel);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(repository, times(1)).findOneByUsername("testUser");
        verify(repository, times(1)).save(any(User.class));
        verify(walletService, times(1)).createInitialUsdtWallet(any(User.class));
    }

    @Test
    void shouldThrowUsernameNotUniqueExceptionWhenUsernameAlreadyExists() {
        when(repository.findOneByUsername(anyString())).thenReturn(Optional.of(user));

        UsernameNotUniqueException exception = assertThrows(UsernameNotUniqueException.class, () -> {
            userService.registerUser(userModel);
        });

        assertEquals("Username is not unique!", exception.getMessage());
        verify(repository, times(1)).findOneByUsername("testUser");
        verify(repository, never()).save(any(User.class));
        verify(walletService, never()).createInitialUsdtWallet(any(User.class));
    }

    @Test
    void shouldThrowTechnicalErrorExceptionWhenUserModelIsIncomplete() {
        UserModel incompleteUserModel = UserModel.builder().build();

        TechnicalErrorException exception = assertThrows(TechnicalErrorException.class, () -> {
            userService.registerUser(incompleteUserModel);
        });

        assertEquals("Passed user model is faulty", exception.getMessage());
        verify(repository, never()).findOneByUsername(anyString());
        verify(repository, never()).save(any(User.class));
        verify(walletService, never()).createInitialUsdtWallet(any(User.class));
    }
}
