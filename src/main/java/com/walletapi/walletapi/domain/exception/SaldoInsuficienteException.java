package com.walletapi.walletapi.domain.exception;

public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(String accountNumber) {
        super("Saldo insuficiente en la cuenta " + accountNumber);
    }
}