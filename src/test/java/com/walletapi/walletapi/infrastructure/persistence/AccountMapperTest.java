package com.walletapi.walletapi.infrastructure.persistence;

import com.walletapi.walletapi.domain.Account;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountMapperTest {

    private final AccountMapper mapper = new AccountMapper();

    @Test
    void deberiaConvertirDeDominioAEntidadYViceversaSinPerderDatos() {
        Account original = new Account("ACC-001", new BigDecimal("100.00"));

        AccountEntity entity = mapper.toEntity(original);
        Account reconstruido = mapper.toDomain(entity);

        assertEquals(original.getId(), reconstruido.getId());
        assertEquals(original.getAccountNumber(), reconstruido.getAccountNumber());
        assertEquals(original.getBalance(), reconstruido.getBalance());
    }
}