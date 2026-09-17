package com.walletapi.walletapi.infrastructure.persistence;

import com.walletapi.walletapi.domain.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountEntity toEntity(Account account) {
        return new AccountEntity(account.getId(), account.getAccountNumber(), account.getBalance());
    }

    public Account toDomain(AccountEntity entity) {
        return Account.reconstruct(entity.getId(), entity.getAccountNumber(), entity.getBalance());
    }
}