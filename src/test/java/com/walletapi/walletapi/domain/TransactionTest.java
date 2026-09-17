package com.walletapi.walletapi.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Nested
    @DisplayName("Creación de depósito")
    class CreacionDeposito {

        @Test
        @DisplayName("debería crear un depósito en estado PENDIENTE, sin cuenta destino")
        void deberiaCrearDepositoPendienteSinCuentaDestino() {
            Transaction tx = Transaction.deposito("ACC-001", new BigDecimal("100.00"));

            assertEquals(TransactionType.DEPOSITO, tx.getType());
            assertEquals(TransactionStatus.PENDIENTE, tx.getStatus());
            assertEquals("ACC-001", tx.getOriginAccountNumber());
            assertNull(tx.getDestinationAccountNumber());
            assertNotNull(tx.getId());
            assertNotNull(tx.getCreatedAt());
        }
    }

    @Nested
    @DisplayName("Creación de retiro")
    class CreacionRetiro {

        @Test
        @DisplayName("debería crear un retiro en estado PENDIENTE, sin cuenta destino")
        void deberiaCrearRetiroPendienteSinCuentaDestino() {
            Transaction tx = Transaction.retiro("ACC-001", new BigDecimal("50.00"));

            assertEquals(TransactionType.RETIRO, tx.getType());
            assertEquals(TransactionStatus.PENDIENTE, tx.getStatus());
            assertNull(tx.getDestinationAccountNumber());
        }
    }

    @Nested
    @DisplayName("Creación de transferencia")
    class CreacionTransferencia {

        @Test
        @DisplayName("debería crear una transferencia con cuenta origen y destino")
        void deberiaCrearTransferenciaConAmbasCuentas() {
            Transaction tx = Transaction.transferencia("ACC-001", "ACC-002", new BigDecimal("30.00"));

            assertEquals(TransactionType.TRANSFERENCIA, tx.getType());
            assertEquals("ACC-001", tx.getOriginAccountNumber());
            assertEquals("ACC-002", tx.getDestinationAccountNumber());
        }

        @Test
        @DisplayName("debería lanzar excepción si la cuenta origen y destino son iguales")
        void deberiaLanzarExcepcionSiOrigenYDestinoSonIguales() {
            assertThrows(IllegalArgumentException.class,
                    () -> Transaction.transferencia("ACC-001", "ACC-001", new BigDecimal("30.00")));
        }
    }

    @Nested
    @DisplayName("Transiciones de estado")
    class TransicionesDeEstado {

        @Test
        @DisplayName("debería cambiar a COMPLETADA al marcarla completada")
        void deberiaCambiarACompletada() {
            Transaction tx = Transaction.deposito("ACC-001", new BigDecimal("100.00"));

            tx.marcarCompletada();

            assertEquals(TransactionStatus.COMPLETADA, tx.getStatus());
        }

        @Test
        @DisplayName("debería cambiar a FALLIDA al marcarla fallida")
        void deberiaCambiarAFallida() {
            Transaction tx = Transaction.retiro("ACC-001", new BigDecimal("50.00"));

            tx.marcarFallida();

            assertEquals(TransactionStatus.FALLIDA, tx.getStatus());
        }
    }
}