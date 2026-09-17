package com.walletapi.walletapi.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void deberiaGuardarYRecuperarUnaCuentaPorNumeroDeCuenta() {
        AccountEntity entity = new AccountEntity(
                UUID.randomUUID().toString(), "ACC-TEST-001", new BigDecimal("150.00"));

        accountRepository.save(entity);

        Optional<AccountEntity> encontrada = accountRepository.findByAccountNumber("ACC-TEST-001");

        assertTrue(encontrada.isPresent());
        assertEquals(new BigDecimal("150.00"), encontrada.get().getBalance());
    }

    @Test
    void deberiaRetornarVacioSiElNumeroDeCuentaNoExiste() {
        Optional<AccountEntity> encontrada = accountRepository.findByAccountNumber("NO-EXISTE");

        assertFalse(encontrada.isPresent());
    }
}