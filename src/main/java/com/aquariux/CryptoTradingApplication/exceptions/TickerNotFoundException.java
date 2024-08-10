package com.aquariux.CryptoTradingApplication.exceptions;

public class TickerNotFoundException extends Exception {

    public TickerNotFoundException(String errorMessage) {
        super(errorMessage);
    }

}
