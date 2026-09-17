package com.walletapi.walletapi.application;

import com.walletapi.walletapi.domain.Account;
import com.walletapi.walletapi.domain.Transaction;
import com.walletapi.walletapi.domain.TransferenciaDomainService;
import com.walletapi.walletapi.infrastructure.persistence.AccountEntity;
import com.walletapi.walletapi.infrastructure.persistence.AccountMapper;
import com.walletapi.walletapi.infrastructure.persistence.AccountRepository;
import com.walletapi.walletapi.infrastructure.persistence.TransactionMapper;
import com.walletapi.walletapi.infrastructure.persistence.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferenciaService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;
    private final TransferenciaDomainService transferenciaDomainService;

    public TransferenciaService(AccountRepository accountRepository,
                                TransactionRepository transactionRepository,
                                AccountMapper accountMapper,
                                TransactionMapper transactionMapper,
                                TransferenciaDomainService transferenciaDomainService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.accountMapper = accountMapper;
        this.transactionMapper = transactionMapper;
        this.transferenciaDomainService = transferenciaDomainService;
    }

    @Transactional
    public Transaction transferir(String origenAccountNumber, String destinoAccountNumber, BigDecimal monto) {
        AccountEntity origenEntity = accountRepository.findByAccountNumber(origenAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta origen no encontrada: " + origenAccountNumber));
        AccountEntity destinoEntity = accountRepository.findByAccountNumber(destinoAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta destino no encontrada: " + destinoAccountNumber));

        Account origen = accountMapper.toDomain(origenEntity);
        Account destino = accountMapper.toDomain(destinoEntity);

        Transaction transaction = transferenciaDomainService.transferir(origen, destino, monto);

        accountRepository.save(accountMapper.toEntity(origen));
        accountRepository.save(accountMapper.toEntity(destino));
        transactionRepository.save(transactionMapper.toEntity(transaction));

        return transaction;
    }
}