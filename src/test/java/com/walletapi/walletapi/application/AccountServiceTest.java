package com.walletapi.walletapi.application;

import com.walletapi.walletapi.domain.Account;
import com.walletapi.walletapi.infrastructure.persistence.AccountEntity;
import com.walletapi.walletapi.infrastructure.persistence.AccountMapper;
import com.walletapi.walletapi.infrastructure.persistence.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    @Nested
    @DisplayName("Crear cuenta")
    class CrearCuenta {

        @Test
        @DisplayName("debería mapear y guardar la cuenta nueva")
        void deberiaCrearYGuardarCuenta() {
            AccountEntity entityGuardada = new AccountEntity("id-1", "ACC-001", new BigDecimal("100.00"));
            when(accountMapper.toEntity(any(Account.class))).thenReturn(entityGuardada);

            Account resultado = accountService.crearCuenta("ACC-001", new BigDecimal("100.00"));

            assertEquals("ACC-001", resultado.getAccountNumber());
            verify(accountRepository).save(entityGuardada);
        }
    }

    @Nested
    @DisplayName("Obtener cuenta")
    class ObtenerCuenta {

        @Test
        @DisplayName("debería devolver la cuenta cuando existe")
        void deberiaDevolverCuentaExistente() {
            AccountEntity entity = new AccountEntity("id-1", "ACC-001", new BigDecimal("50.00"));
            Account cuentaDominio = Account.reconstruct("id-1", "ACC-001", new BigDecimal("50.00"));

            when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(entity));
            when(accountMapper.toDomain(entity)).thenReturn(cuentaDominio);

            Account resultado = accountService.obtenerCuenta("ACC-001");

            assertEquals("ACC-001", resultado.getAccountNumber());
            assertEquals(new BigDecimal("50.00"), resultado.getBalance());
        }

        @Test
        @DisplayName("debería lanzar excepción si la cuenta no existe")
        void deberiaLanzarExcepcionSiNoExiste() {
            when(accountRepository.findByAccountNumber("ACC-999")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> accountService.obtenerCuenta("ACC-999"));
        }
    }

    @Nested
    @DisplayName("Depositar")
    class Depositar {

        @Test
        @DisplayName("debería aumentar el saldo y guardar la cuenta actualizada")
        void deberiaDepositarYGuardar() {
            AccountEntity entity = new AccountEntity("id-1", "ACC-001", new BigDecimal("100.00"));
            Account cuentaDominio = Account.reconstruct("id-1", "ACC-001", new BigDecimal("100.00"));

            when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(entity));
            when(accountMapper.toDomain(entity)).thenReturn(cuentaDominio);
            when(accountMapper.toEntity(cuentaDominio)).thenReturn(entity);

            Account resultado = accountService.depositar("ACC-001", new BigDecimal("50.00"));

            assertEquals(new BigDecimal("150.00"), resultado.getBalance());
            verify(accountRepository).save(entity);
        }
    }
}