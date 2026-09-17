package com.walletapi.walletapi.application;

import com.walletapi.walletapi.domain.*;
import com.walletapi.walletapi.infrastructure.persistence.*;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferenciaServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private TransferenciaDomainService transferenciaDomainService;

    @InjectMocks
    private TransferenciaService transferenciaService;

    @Nested
    @DisplayName("Transferencia exitosa")
    class TransferenciaExitosa {

        @Test
        @DisplayName("debería orquestar la transferencia y guardar los tres resultados")
        void deberiaOrquestarYGuardarTodo() {
            AccountEntity origenEntity = new AccountEntity("id-1", "ACC-001", new BigDecimal("100.00"));
            AccountEntity destinoEntity = new AccountEntity("id-2", "ACC-002", new BigDecimal("20.00"));
            Account origen = Account.reconstruct("id-1", "ACC-001", new BigDecimal("100.00"));
            Account destino = Account.reconstruct("id-2", "ACC-002", new BigDecimal("20.00"));
            Transaction transaccionCompletada = Transaction.transferencia("ACC-001", "ACC-002", new BigDecimal("30.00"));
            transaccionCompletada.marcarCompletada();
            TransactionEntity transactionEntity = mock(TransactionEntity.class);

            when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(origenEntity));
            when(accountRepository.findByAccountNumber("ACC-002")).thenReturn(Optional.of(destinoEntity));
            when(accountMapper.toDomain(origenEntity)).thenReturn(origen);
            when(accountMapper.toDomain(destinoEntity)).thenReturn(destino);
            when(transferenciaDomainService.transferir(origen, destino, new BigDecimal("30.00")))
                    .thenReturn(transaccionCompletada);
            when(accountMapper.toEntity(origen)).thenReturn(origenEntity);
            when(accountMapper.toEntity(destino)).thenReturn(destinoEntity);
            when(transactionMapper.toEntity(transaccionCompletada)).thenReturn(transactionEntity);

            Transaction resultado = transferenciaService.transferir("ACC-001", "ACC-002", new BigDecimal("30.00"));

            assertEquals(TransactionStatus.COMPLETADA, resultado.getStatus());
            verify(accountRepository).save(origenEntity);
            verify(accountRepository).save(destinoEntity);
            verify(transactionRepository).save(transactionEntity);
        }
    }

    @Nested
    @DisplayName("Cuentas no encontradas")
    class CuentasNoEncontradas {

        @Test
        @DisplayName("debería lanzar excepción si la cuenta origen no existe, sin llamar al dominio")
        void deberiaLanzarExcepcionSiOrigenNoExiste() {
            when(accountRepository.findByAccountNumber("ACC-999")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> transferenciaService.transferir("ACC-999", "ACC-002", new BigDecimal("30.00")));

            verifyNoInteractions(transferenciaDomainService);
        }

        @Test
        @DisplayName("debería lanzar excepción si la cuenta destino no existe, sin llamar al dominio")
        void deberiaLanzarExcepcionSiDestinoNoExiste() {
            AccountEntity origenEntity = new AccountEntity("id-1", "ACC-001", new BigDecimal("100.00"));
            when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(origenEntity));
            when(accountRepository.findByAccountNumber("ACC-999")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> transferenciaService.transferir("ACC-001", "ACC-999", new BigDecimal("30.00")));

            verifyNoInteractions(transferenciaDomainService);
        }
    }
}