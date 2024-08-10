package com.aquariux.CryptoTradingApplication.services;

import com.aquariux.CryptoTradingApplication.entities.User;
import com.aquariux.CryptoTradingApplication.entities.Wallet;
import com.aquariux.CryptoTradingApplication.exceptions.InsufficientFundsException;
import com.aquariux.CryptoTradingApplication.exceptions.WalletNotFoundException;
import com.aquariux.CryptoTradingApplication.models.WalletModel;
import com.aquariux.CryptoTradingApplication.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.INITIAL_USDT_BALANCE;
import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.USDT;
import static com.aquariux.CryptoTradingApplication.mappers.WalletMapper.mapToEntity;
import static com.aquariux.CryptoTradingApplication.mappers.WalletMapper.mapToModel;

@Service
@Slf4j
public class WalletService {

    private final WalletRepository repository;

    @Autowired
    public WalletService(WalletRepository repository) {
        this.repository = repository;
    }

    public WalletModel retrieveWalletByUserIdAndCrypto(Long userId, String crypto) throws WalletNotFoundException {
        Optional<Wallet> optional = repository.findByUserIdAndCrypto(userId, crypto);
        if (optional.isEmpty()) {
            log.error("User: {} does not have an existing wallet for {}", userId, crypto);
            throw new WalletNotFoundException("Wallet not found for user: " + userId + " for crypto: " + "crypto");
        }

        return mapToModel(optional.get());
    }

    @Transactional
    public void increaseWalletBalance(WalletModel wallet, BigDecimal amount) throws WalletNotFoundException {
        if (Objects.isNull(wallet)) {
            log.error("Wallet is null");
            throw new WalletNotFoundException("Wallet is not found");
        }

        log.info("Increasing wallet balance for user: {}, for crypto: {} by {}", wallet.getUser().getId(), wallet.getCrypto(), amount);
        wallet.setBalance(wallet.getBalance().add(amount));
        repository.save(mapToEntity(wallet));
    }

    @Transactional
    public void decreaseWalletBalance(WalletModel wallet, BigDecimal amount) throws InsufficientFundsException, WalletNotFoundException {
        if (Objects.isNull(wallet)) {
            log.error("Wallet is null");
            throw new WalletNotFoundException("Wallet is not found");
        }

        log.info("Decreasing wallet balance for user: {}, for crypto: {}, by {}", wallet.getUser().getId(), wallet.getCrypto(), amount);
        if (wallet.getBalance().compareTo(amount) < 0) {
            log.error("Insufficient funds in wallet for crypto: {}, owned by user: {}", wallet.getCrypto(), wallet.getUser().getId());
            throw new InsufficientFundsException("Insufficient funds in wallet!");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        repository.save(mapToEntity(wallet));
    }

    @Transactional
    public Wallet createUserWallet(User user, String crypto) {
        Wallet wallet =  Wallet.builder()
                .user(user)
                .crypto(crypto)
                .amount(BigDecimal.ZERO) // initial creation and save first before we assign value
                .build();

        return repository.save(wallet);
    }

    public Wallet createInitialUsdtWallet(User user) {
        Wallet wallet = Wallet.builder()
                .user(user)
                .amount(new BigDecimal(INITIAL_USDT_BALANCE))
                .crypto(USDT)
                .build();

        return repository.save(wallet);
    }

}
