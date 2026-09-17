package com.walletapi.walletapi.web.dto;

import com.walletapi.walletapi.domain.Account;

import java.math.BigDecimal;

public record AccountResponse(String id, String accountNumber, BigDecimal balance) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getAccountNumber(), account.getBalance());
    }
}