package com.walletapi.walletapi.application;

import com.walletapi.walletapi.domain.Account;
import com.walletapi.walletapi.infrastructure.persistence.AccountEntity;
import com.walletapi.walletapi.infrastructure.persistence.AccountMapper;
import com.walletapi.walletapi.infrastructure.persistence.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountService(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Transactional
    public Account crearCuenta(String accountNumber, BigDecimal saldoInicial) {
        Account account = new Account(accountNumber, saldoInicial);
        AccountEntity entity = accountMapper.toEntity(account);
        accountRepository.save(entity);
        return account;
    }

    @Transactional(readOnly = true)
    public Account obtenerCuenta(String accountNumber) {
        AccountEntity entity = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + accountNumber));
        return accountMapper.toDomain(entity);
    }

    @Transactional
    public Account depositar(String accountNumber, BigDecimal monto) {
        AccountEntity entity = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + accountNumber));

        Account account = accountMapper.toDomain(entity);
        account.deposit(monto);

        accountRepository.save(accountMapper.toEntity(account));
        return account;
    }

    @Transactional
    public Account retirar(String accountNumber, BigDecimal monto) {
        AccountEntity entity = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + accountNumber));

        Account account = accountMapper.toDomain(entity);
        account.withdraw(monto);

        accountRepository.save(accountMapper.toEntity(account));
        return account;
    }
}