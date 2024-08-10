package com.aquariux.CryptoTradingApplication.services;

import com.aquariux.CryptoTradingApplication.constants.TradeActions;
import com.aquariux.CryptoTradingApplication.entities.Transaction;
import com.aquariux.CryptoTradingApplication.entities.Wallet;
import com.aquariux.CryptoTradingApplication.exceptions.InsufficientFundsException;
import com.aquariux.CryptoTradingApplication.exceptions.TechnicalErrorException;
import com.aquariux.CryptoTradingApplication.exceptions.TickerNotFoundException;
import com.aquariux.CryptoTradingApplication.exceptions.WalletNotFoundException;
import com.aquariux.CryptoTradingApplication.mappers.TransactionMapper;
import com.aquariux.CryptoTradingApplication.mappers.UserMapper;
import com.aquariux.CryptoTradingApplication.mappers.WalletMapper;
import com.aquariux.CryptoTradingApplication.models.CryptoPriceModel;
import com.aquariux.CryptoTradingApplication.models.TransactionModel;
import com.aquariux.CryptoTradingApplication.models.UserModel;
import com.aquariux.CryptoTradingApplication.models.WalletModel;
import com.aquariux.CryptoTradingApplication.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.aquariux.CryptoTradingApplication.constants.ApiConstants.USDT;

@Service
@Slf4j
public class TransactionService {

    private final PriceRetrievalService priceService;

    private final WalletService walletService;

    private final UserService userService;

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(PriceRetrievalService priceService, WalletService walletService, UserService userService, TransactionRepository transactionRepository) {
        this.priceService = priceService;
        this.walletService = walletService;
        this.userService = userService;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionModel executeTransaction(Long userId, String ticker, BigDecimal amount, TradeActions action) throws TechnicalErrorException {
        // Retrieve latest prices
        try {
            CryptoPriceModel latestPrice = priceService.getLatestPriceForTicker(ticker);
            BigDecimal tickerTradePrice = action.equals(TradeActions.BUY) ? latestPrice.getAskPrice().setScale(2, RoundingMode.HALF_UP) : latestPrice.getBidPrice().setScale(2, RoundingMode.HALF_UP);
            BigDecimal amountRequiredForTrade = tickerTradePrice.multiply(amount).setScale(2, RoundingMode.HALF_UP);

            UserModel user = userService.getUserById(userId);

            // Retrieve different wallet based on trade action
            // BUY = USDT wallet, SELL = ticker wallet
            // extract this logic if time permits
            if (action.equals(TradeActions.BUY)) {
                // reduce USDT wallet, increase crypto wallet
                WalletModel usdtWallet = walletService.retrieveWalletByUserIdAndCrypto(userId, USDT);
                walletService.decreaseWalletBalance(usdtWallet, amountRequiredForTrade);

                WalletModel cryptoWallet = walletService.retrieveWalletByUserIdAndCrypto(userId, ticker);
                if (Objects.isNull(cryptoWallet)) {
                    log.error("User has no crypto wallet for ticker: {}", ticker);
                    Wallet createdWallet = walletService.createUserWallet(UserMapper.mapToEntity(user), ticker);
                    walletService.increaseWalletBalance(WalletMapper.mapToModel(createdWallet), amount);
                } else {
                    walletService.increaseWalletBalance(cryptoWallet, amount);
                }
            } else {
                // Decrease crypto wallet, increase USDT wallet
                WalletModel cryptoWallet = walletService.retrieveWalletByUserIdAndCrypto(userId, ticker);
                if (Objects.isNull(cryptoWallet)) {
                    log.error("User: {} does not have wallet for ticker: {}", userId, ticker);
                    throw new WalletNotFoundException("User does not have wallet with crypto to sell!");
                }
                walletService.decreaseWalletBalance(cryptoWallet, amount);
                WalletModel usdtWallet = walletService.retrieveWalletByUserIdAndCrypto(userId, USDT);
                walletService.increaseWalletBalance(usdtWallet, amountRequiredForTrade);
            }

            // create a new transaction record
            Transaction transaction = Transaction.builder()
                    .user(UserMapper.mapToEntity(user))
                    .cryptoPair(ticker)
                    .price(tickerTradePrice)
                    .amount(amount)
                    .tradeAction(action)
                    .timeStamp(LocalDateTime.now())
                    .build();

            return TransactionMapper.mapToModel(transactionRepository.save(transaction));

        } catch (TickerNotFoundException | WalletNotFoundException | InsufficientFundsException e) {
            log.error("Transaction failed for user: {}, ticker: {}, action: {}. Error: {}", userId, ticker, action, e.getMessage());
            throw new TechnicalErrorException(e.getMessage());
        }
    }

}
