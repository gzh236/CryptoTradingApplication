package com.aquariux.CryptoTradingApplication.services;

import com.aquariux.CryptoTradingApplication.entities.User;
import com.aquariux.CryptoTradingApplication.mappers.UserMapper;
import com.aquariux.CryptoTradingApplication.models.UserModel;
import com.aquariux.CryptoTradingApplication.models.WalletModel;
import com.aquariux.CryptoTradingApplication.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.USDT;

@Service
@Slf4j
public class UserService {

    private final UserRepository repository;

    private final WalletService walletService;

    public UserService(UserRepository repository, WalletService walletService) {
        this.repository = repository;
        this.walletService = walletService;
    }

    public UserModel getUserById(Long userId) {
        User user = repository.getReferenceById(userId);
        return UserMapper.mapToModel(user);
    }

    @Transactional
    public UserModel registerUser(UserModel userModel) {
        // Save the user
        User user = UserMapper.mapToEntity(userModel);
        User savedUser = repository.save(user);

        walletService.createInitialUsdtWallet(savedUser);

        // Return the saved user model
        return UserMapper.mapToModel(savedUser);
    }
}
