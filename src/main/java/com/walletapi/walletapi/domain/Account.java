package com.walletapi.walletapi.domain;

import com.walletapi.walletapi.domain.exception.SaldoInsuficienteException;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {

    private final String id;
    private final String accountNumber;
    private BigDecimal balance;

    public Account(String accountNumber, BigDecimal initialBalance) {
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }
        this.id = UUID.randomUUID().toString();
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    private Account(String id, String accountNumber, BigDecimal balance) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public static Account reconstruct(String id, String accountNumber, BigDecimal balance) {
        return new Account(id, accountNumber, balance);
    }

    public void deposit(BigDecimal amount) {
        validateAmount(amount);
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        validateAmount(amount);
        if (amount.compareTo(this.balance) > 0) {
            throw new SaldoInsuficienteException(accountNumber);
        }
        this.balance = this.balance.subtract(amount);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
    }

    public String getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getBalance() { return balance; }
}