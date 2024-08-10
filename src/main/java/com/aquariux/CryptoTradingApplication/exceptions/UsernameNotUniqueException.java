package com.aquariux.CryptoTradingApplication.exceptions;

public class UsernameNotUniqueException extends Exception {

    public UsernameNotUniqueException(String errorMessage) {
        super(errorMessage);
    }
}
