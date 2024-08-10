package com.aquariux.CryptoTradingApplication.controllers;

import com.aquariux.CryptoTradingApplication.exceptions.WalletNotFoundException;
import com.aquariux.CryptoTradingApplication.models.WalletModel;
import com.aquariux.CryptoTradingApplication.services.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@Slf4j
public class WalletController {

    private final WalletService service;

    @Autowired
    public WalletController(WalletService service) {
        this.service = service;
    }

    @GetMapping(value = "byUser", params = "userId")
    public ResponseEntity<List<WalletModel>> getUserWallets(@RequestParam Long userId) {
        log.info("Retrieving wallets for user: {}", userId);
        try {
            List<WalletModel> models = service.retrieveWalletsByUserId(userId);
            return ResponseEntity.ok(models);
        } catch (WalletNotFoundException e) {
            log.error("No wallets found for user: {}, {}", userId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

}
