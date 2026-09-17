package com.walletapi.walletapi.infrastructure.persistence;

import com.walletapi.walletapi.domain.Transaction;
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
class TransactionMapperTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionMapper mapper;

    @Test
    void deberiaConvertirDepositoADominioAEntidad() {
        AccountEntity cuenta = new AccountEntity("acc-1", "ACC-001", new BigDecimal("100.00"));
        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(cuenta));

        Transaction deposito = Transaction.deposito("ACC-001", new BigDecimal("50.00"));

        TransactionEntity entity = mapper.toEntity(deposito);

        assertEquals(deposito.getId(), entity.getId());
        assertEquals(cuenta, entity.getOriginAccount());
        assertNull(entity.getDestinationAccount());
        verify(accountRepository).findByAccountNumber("ACC-001");
    }

    @Test
    void deberiaLanzarExcepcionSiLaCuentaOrigenNoExiste() {
        when(accountRepository.findByAccountNumber("ACC-999")).thenReturn(Optional.empty());

        Transaction deposito = Transaction.deposito("ACC-999", new BigDecimal("50.00"));

        assertThrows(IllegalStateException.class, () -> mapper.toEntity(deposito));
    }
}