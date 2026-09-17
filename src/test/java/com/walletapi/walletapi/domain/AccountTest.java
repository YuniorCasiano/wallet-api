package com.walletapi.walletapi.domain;

import com.walletapi.walletapi.domain.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Nested
    @DisplayName("Creación de cuenta")
    class Creacion {

        @Test
        @DisplayName("debería crear la cuenta con el saldo inicial dado")
        void deberiaCrearCuentaConSaldoInicial() {
            Account account = new Account("ACC-001", new BigDecimal("100.00"));

            assertEquals(new BigDecimal("100.00"), account.getBalance());
            assertEquals("ACC-001", account.getAccountNumber());
        }

        @Test
        @DisplayName("debería lanzar excepción si el saldo inicial es negativo")
        void deberiaLanzarExcepcionSiSaldoInicialEsNegativo() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Account("ACC-001", new BigDecimal("-10.00")));
        }
    }

    @Nested
    @DisplayName("Depósitos")
    class Depositos {

        @Test
        @DisplayName("debería aumentar el saldo al depositar un monto válido")
        void deberiaAumentarSaldoAlDepositar() {
            Account account = new Account("ACC-001", new BigDecimal("50.00"));

            account.deposit(new BigDecimal("25.00"));

            assertEquals(new BigDecimal("75.00"), account.getBalance());
        }

        @Test
        @DisplayName("debería lanzar excepción si el monto a depositar es cero o negativo")
        void deberiaLanzarExcepcionSiDepositoEsInvalido() {
            Account account = new Account("ACC-001", new BigDecimal("50.00"));

            assertThrows(IllegalArgumentException.class,
                    () -> account.deposit(BigDecimal.ZERO));
            assertThrows(IllegalArgumentException.class,
                    () -> account.deposit(new BigDecimal("-5.00")));
        }
    }

    @Nested
    @DisplayName("Retiros")
    class Retiros {

        @Test
        @DisplayName("debería disminuir el saldo al retirar un monto válido")
        void deberiaDisminuirSaldoAlRetirar() {
            Account account = new Account("ACC-001", new BigDecimal("100.00"));

            account.withdraw(new BigDecimal("40.00"));

            assertEquals(new BigDecimal("60.00"), account.getBalance());
        }

        @Test
        @DisplayName("debería lanzar SaldoInsuficienteException si el retiro supera el saldo")
        void deberiaLanzarExcepcionSiRetiroSuperaElSaldo() {
            Account account = new Account("ACC-001", new BigDecimal("30.00"));

            assertThrows(SaldoInsuficienteException.class,
                    () -> account.withdraw(new BigDecimal("50.00")));
        }

        @Test
        @DisplayName("debería lanzar excepción si el monto a retirar es cero o negativo")
        void deberiaLanzarExcepcionSiRetiroEsInvalido() {
            Account account = new Account("ACC-001", new BigDecimal("30.00"));

            assertThrows(IllegalArgumentException.class,
                    () -> account.withdraw(BigDecimal.ZERO));
        }
    }
}