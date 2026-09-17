package com.walletapi.walletapi.domain;

import java.math.BigDecimal;

public class TransferenciaDomainService {

    public Transaction transferir(Account origen, Account destino, BigDecimal monto) {
        Transaction transaction = Transaction.transferencia(
                origen.getAccountNumber(), destino.getAccountNumber(), monto);

        origen.withdraw(monto);

        try {
            destino.deposit(monto);
        } catch (RuntimeException e) {
            origen.deposit(monto);
            transaction.marcarFallida();
            throw e;
        }

        transaction.marcarCompletada();
        return transaction;
    }
}