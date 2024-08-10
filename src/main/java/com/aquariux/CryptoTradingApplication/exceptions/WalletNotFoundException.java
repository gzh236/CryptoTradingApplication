package com.aquariux.CryptoTradingApplication.exceptions;

public class WalletNotFoundException extends Exception {

    public WalletNotFoundException(String errorMessage) {
        super(errorMessage);
    }

}
