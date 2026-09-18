package com.walletapi.walletapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class WalletApiIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void deberiaCompletarElFlujoDeCrearDepositarYTransferirEntreCuentasReales() {
        crearCuenta("ACC-INT-001", new BigDecimal("100.00"));
        crearCuenta("ACC-INT-002", new BigDecimal("0.00"));

        depositar("ACC-INT-001", new BigDecimal("50.00"));

        Map<String, Object> transferenciaBody = Map.of(
            "cuentaOrigen", "ACC-INT-001",
            "cuentaDestino", "ACC-INT-002",
            "monto", new BigDecimal("30.00")
        );
        ResponseEntity<Map> transferenciaResponse = restTemplate.postForEntity(
            "/api/transferencias", transferenciaBody, Map.class);

        assertEquals(HttpStatus.CREATED, transferenciaResponse.getStatusCode());
        assertEquals("COMPLETADA", transferenciaResponse.getBody().get("status"));

        Map<String, Object> origenActualizada = restTemplate.getForObject(
            "/api/cuentas/ACC-INT-001", Map.class);
        Map<String, Object> destinoActualizada = restTemplate.getForObject(
            "/api/cuentas/ACC-INT-002", Map.class);

        assertEquals(120.0, ((Number) origenActualizada.get("balance")).doubleValue());
        assertEquals(30.0, ((Number) destinoActualizada.get("balance")).doubleValue());
    }

    private void crearCuenta(String accountNumber, BigDecimal saldoInicial) {
        Map<String, Object> body = Map.of("accountNumber", accountNumber, "saldoInicial", saldoInicial);
        ResponseEntity<Map> response = restTemplate.postForEntity("/api/cuentas", body, Map.class);
        System.out.println("Respuesta crearCuenta (" + accountNumber + "): " + response.getStatusCode() + " -> " + response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    private void depositar(String accountNumber, BigDecimal monto) {
        Map<String, Object> body = Map.of("monto", monto);
        ResponseEntity<Map> response = restTemplate.postForEntity(
            "/api/cuentas/" + accountNumber + "/depositos", body, Map.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}