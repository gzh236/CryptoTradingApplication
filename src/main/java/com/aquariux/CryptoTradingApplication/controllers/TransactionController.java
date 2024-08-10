package com.aquariux.CryptoTradingApplication.controllers;

import com.aquariux.CryptoTradingApplication.exceptions.TechnicalErrorException;
import com.aquariux.CryptoTradingApplication.models.TradeRequestModel;
import com.aquariux.CryptoTradingApplication.models.TransactionModel;
import com.aquariux.CryptoTradingApplication.services.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/transactions")
@Slf4j
public class TransactionController {

    private final TransactionService service;

    @Autowired
    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping(value = "/trade")
    public ResponseEntity<TransactionModel> makeTradeAction(@RequestBody TradeRequestModel tradeRequestModel) {
        try {
            return ResponseEntity.ok(
                    service.executeTransaction(tradeRequestModel.getUserId(), tradeRequestModel.getTicker(), tradeRequestModel.getAmount(), tradeRequestModel.getAction()));
        } catch (TechnicalErrorException e) {
            log.error("Exception while making trade action: {} for user: {}", tradeRequestModel.getAction().name(), tradeRequestModel.getUserId());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/history")
    public ResponseEntity<List<TransactionModel>> getUserTransactionHistory(@RequestParam Long userId) {
        List<TransactionModel> res = service.retrieveUserTransactionHistory(userId);
        if (Objects.isNull(res) || res.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(res);
    }


}
