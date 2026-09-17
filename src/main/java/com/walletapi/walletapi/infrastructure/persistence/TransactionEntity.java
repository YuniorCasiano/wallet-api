package com.walletapi.walletapi.infrastructure.persistence;

import com.walletapi.walletapi.domain.TransactionStatus;
import com.walletapi.walletapi.domain.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_account_id", nullable = false)
    private AccountEntity originAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id")
    private AccountEntity destinationAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected TransactionEntity() {
        // Constructor vacío requerido por JPA/Hibernate
    }

    public TransactionEntity(String id, TransactionType type, BigDecimal amount,
                             AccountEntity originAccount, AccountEntity destinationAccount,
                             TransactionStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public AccountEntity getOriginAccount() { return originAccount; }
    public AccountEntity getDestinationAccount() { return destinationAccount; }
    public TransactionStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setStatus(TransactionStatus status) { this.status = status; }
}