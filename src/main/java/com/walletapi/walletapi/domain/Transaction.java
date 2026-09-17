package com.walletapi.walletapi.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {

    private final String id;
    private final TransactionType type;
    private final BigDecimal amount;
    private final String originAccountNumber;
    private final String destinationAccountNumber;
    private final LocalDateTime createdAt;
    private TransactionStatus status;

    private Transaction(TransactionType type, BigDecimal amount,
                        String originAccountNumber, String destinationAccountNumber) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.amount = amount;
        this.originAccountNumber = originAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
        this.createdAt = LocalDateTime.now();
        this.status = TransactionStatus.PENDIENTE;
    }

    public static Transaction deposito(String accountNumber, BigDecimal amount) {
        return new Transaction(TransactionType.DEPOSITO, amount, accountNumber, null);
    }

    public static Transaction retiro(String accountNumber, BigDecimal amount) {
        return new Transaction(TransactionType.RETIRO, amount, accountNumber, null);
    }

    public static Transaction transferencia(String originAccountNumber, String destinationAccountNumber, BigDecimal amount) {
        if (originAccountNumber.equals(destinationAccountNumber)) {
            throw new IllegalArgumentException("La cuenta origen y destino no pueden ser la misma");
        }
        return new Transaction(TransactionType.TRANSFERENCIA, amount, originAccountNumber, destinationAccountNumber);
    }

    public static Transaction reconstruct(String id, TransactionType type, BigDecimal amount,
                                          String originAccountNumber, String destinationAccountNumber,
                                          TransactionStatus status, LocalDateTime createdAt) {
        Transaction tx = new Transaction(type, amount, originAccountNumber, destinationAccountNumber);
        tx.status = status;
        return tx;
    }

    public void marcarCompletada() {
        this.status = TransactionStatus.COMPLETADA;
    }

    public void marcarFallida() {
        this.status = TransactionStatus.FALLIDA;
    }

    public String getId() { return id; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getOriginAccountNumber() { return originAccountNumber; }
    public String getDestinationAccountNumber() { return destinationAccountNumber; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public TransactionStatus getStatus() { return status; }
}