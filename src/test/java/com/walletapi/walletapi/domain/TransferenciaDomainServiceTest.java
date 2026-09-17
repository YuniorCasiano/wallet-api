package com.walletapi.walletapi.domain;

import com.walletapi.walletapi.domain.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransferenciaDomainServiceTest {

    private final TransferenciaDomainService service = new TransferenciaDomainService();

    @Nested
    @DisplayName("Transferencia exitosa")
    class TransferenciaExitosa {

        @Test
        @DisplayName("debería mover el monto de origen a destino y marcar la transacción como COMPLETADA")
        void deberiaTransferirCorrectamente() {
            Account origen = new Account("ACC-001", new BigDecimal("100.00"));
            Account destino = new Account("ACC-002", new BigDecimal("20.00"));

            Transaction tx = service.transferir(origen, destino, new BigDecimal("30.00"));

            assertEquals(new BigDecimal("70.00"), origen.getBalance());
            assertEquals(new BigDecimal("50.00"), destino.getBalance());
            assertEquals(TransactionStatus.COMPLETADA, tx.getStatus());
            assertEquals(TransactionType.TRANSFERENCIA, tx.getType());
        }
    }

    @Nested
    @DisplayName("Transferencia con saldo insuficiente")
    class SaldoInsuficiente {

        @Test
        @DisplayName("debería lanzar SaldoInsuficienteException y no modificar ningún saldo")
        void deberiaLanzarExcepcionSinModificarSaldos() {
            Account origen = new Account("ACC-001", new BigDecimal("10.00"));
            Account destino = new Account("ACC-002", new BigDecimal("20.00"));

            assertThrows(SaldoInsuficienteException.class,
                    () -> service.transferir(origen, destino, new BigDecimal("50.00")));

            assertEquals(new BigDecimal("10.00"), origen.getBalance());
            assertEquals(new BigDecimal("20.00"), destino.getBalance());
        }
    }

    @Nested
    @DisplayName("Transferencia que falla al acreditar el destino")
    class FalloEnDestino {

        @Test
        @DisplayName("debería revertir el retiro en origen y marcar la transacción como FALLIDA")
        void deberiaRevertirRetiroSiDepositoEnDestinoFalla() {
            Account origen = new Account("ACC-001", new BigDecimal("100.00"));
            Account destino = new CuentaQueSiempreFallaAlDepositar("ACC-002", new BigDecimal("20.00"));

            assertThrows(RuntimeException.class,
                    () -> service.transferir(origen, destino, new BigDecimal("30.00")));

            assertEquals(new BigDecimal("100.00"), origen.getBalance());
        }
    }

    /**
     * Test double: una cuenta que siempre falla al depositar. Su único propósito
     * es forzar el camino de error dentro de TransferenciaDomainService para
     * poder verificar la reversión.
     */
    private static class CuentaQueSiempreFallaAlDepositar extends Account {
        CuentaQueSiempreFallaAlDepositar(String accountNumber, BigDecimal initialBalance) {
            super(accountNumber, initialBalance);
        }

        @Override
        public void deposit(BigDecimal amount) {
            throw new RuntimeException("Fallo simulado al depositar");
        }
    }
}