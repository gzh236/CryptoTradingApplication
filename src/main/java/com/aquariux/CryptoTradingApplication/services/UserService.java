package com.aquariux.CryptoTradingApplication.services;

import com.aquariux.CryptoTradingApplication.entities.User;
import com.aquariux.CryptoTradingApplication.entities.Wallet;
import com.aquariux.CryptoTradingApplication.exceptions.TechnicalErrorException;
import com.aquariux.CryptoTradingApplication.exceptions.UsernameNotUniqueException;
import com.aquariux.CryptoTradingApplication.mappers.UserMapper;
import com.aquariux.CryptoTradingApplication.mappers.WalletMapper;
import com.aquariux.CryptoTradingApplication.models.UserModel;
import com.aquariux.CryptoTradingApplication.models.WalletModel;
import com.aquariux.CryptoTradingApplication.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public UserModel registerUser(UserModel userModel) throws TechnicalErrorException, UsernameNotUniqueException {
        User user = UserMapper.mapToEntity(userModel);
        if (Objects.isNull(user) || Objects.isNull(userModel.getUsername())) {
            log.error("Passed UserModel is not complete or empty");
            throw new TechnicalErrorException("Passed user model is faulty");
        }

        // duplicate check for username
        Optional<User> checkForDuplicateOpt = repository.findOneByUsername(userModel.getUsername());
        if (checkForDuplicateOpt.isPresent()) {
            log.error("Username: {} is already taken", userModel.getUsername());
            throw new UsernameNotUniqueException("Username is not unique!");
        }

        User savedUser = repository.save(user);

        Wallet wallet = walletService.createInitialUsdtWallet(savedUser);
        savedUser.setWallets(List.of(wallet));

        return UserMapper.mapToModel(savedUser);
    }
}
