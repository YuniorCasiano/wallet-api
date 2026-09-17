package com.walletapi.walletapi.infrastructure.persistence;

import com.walletapi.walletapi.domain.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    private final AccountRepository accountRepository;

    public TransactionMapper(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public TransactionEntity toEntity(Transaction transaction) {
        AccountEntity origin = accountRepository.findByAccountNumber(transaction.getOriginAccountNumber())
                .orElseThrow(() -> new IllegalStateException("Cuenta origen no encontrada: " + transaction.getOriginAccountNumber()));

        AccountEntity destination = transaction.getDestinationAccountNumber() != null
                ? accountRepository.findByAccountNumber(transaction.getDestinationAccountNumber())
                .orElseThrow(() -> new IllegalStateException("Cuenta destino no encontrada: " + transaction.getDestinationAccountNumber()))
                : null;

        return new TransactionEntity(transaction.getId(), transaction.getType(), transaction.getAmount(),
                origin, destination, transaction.getStatus(), transaction.getCreatedAt());
    }

    public Transaction toDomain(TransactionEntity entity) {
        return Transaction.reconstruct(entity.getId(), entity.getType(), entity.getAmount(),
                entity.getOriginAccount().getAccountNumber(),
                entity.getDestinationAccount() != null ? entity.getDestinationAccount().getAccountNumber() : null,
                entity.getStatus(), entity.getCreatedAt());
    }
}