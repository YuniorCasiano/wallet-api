package com.walletapi.walletapi.web.dto;

import com.walletapi.walletapi.domain.Transaction;
import com.walletapi.walletapi.domain.TransactionStatus;
import com.walletapi.walletapi.domain.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String id,
        TransactionType type,
        BigDecimal amount,
        String cuentaOrigen,
        String cuentaDestino,
        TransactionStatus status,
        LocalDateTime createdAt
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getOriginAccountNumber(),
                transaction.getDestinationAccountNumber(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );
    }
}