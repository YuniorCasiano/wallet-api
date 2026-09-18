package com.walletapi.walletapi.web;

import com.walletapi.walletapi.web.dto.CrearCuentaRequest;
import com.walletapi.walletapi.web.dto.MontoRequest;
import tools.jackson.databind.ObjectMapper;
import com.walletapi.walletapi.application.AccountService;
import com.walletapi.walletapi.domain.Account;
import com.walletapi.walletapi.domain.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @Nested
    @DisplayName("Crear cuenta")
    class CrearCuenta {

        @Test
        @DisplayName("debería devolver 201 y la cuenta creada")
        void deberiaCrearCuentaYDevolver201() throws Exception {
            Account cuentaCreada = new Account("ACC-001", new BigDecimal("100.00"));
            when(accountService.crearCuenta(eq("ACC-001"), eq(new BigDecimal("100.00"))))
                    .thenReturn(cuentaCreada);

            CrearCuentaRequest request = new CrearCuentaRequest("ACC-001", new BigDecimal("100.00"));

            mockMvc.perform(post("/api/cuentas")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accountNumber").value("ACC-001"))
                    .andExpect(jsonPath("$.balance").value(100.00));
        }

        @Test
        @DisplayName("debería devolver 400 si el saldo inicial es negativo")
        void deberiaDevolver400SiSaldoEsNegativo() throws Exception {
            CrearCuentaRequest request = new CrearCuentaRequest("ACC-001", new BigDecimal("-10.00"));

            mockMvc.perform(post("/api/cuentas")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(accountService);
        }
    }

    @Nested
    @DisplayName("Depositar")
    class Depositar {

        @Test
        @DisplayName("debería devolver 200 y el saldo actualizado")
        void deberiaDepositarYDevolver200() throws Exception {
            Account cuentaActualizada = new Account("ACC-001", new BigDecimal("150.00"));
            when(accountService.depositar(eq("ACC-001"), eq(new BigDecimal("50.00"))))
                    .thenReturn(cuentaActualizada);

            MontoRequest request = new MontoRequest(new BigDecimal("50.00"));

            mockMvc.perform(post("/api/cuentas/ACC-001/depositos")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.balance").value(150.00));
        }

        @Test
        @DisplayName("debería devolver 409 si el retiro correspondiente supera el saldo")
        void deberiaDevolver409SiSaldoEsInsuficiente() throws Exception {
            when(accountService.retirar(eq("ACC-001"), eq(new BigDecimal("500.00"))))
                    .thenThrow(new SaldoInsuficienteException("ACC-001"));

            MontoRequest request = new MontoRequest(new BigDecimal("500.00"));

            mockMvc.perform(post("/api/cuentas/ACC-001/retiros")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").exists());
        }
    }

    @Nested
    @DisplayName("Obtener cuenta")
    class ObtenerCuenta {

        @Test
        @DisplayName("debería devolver 400 si la cuenta no existe")
        void deberiaDevolver400SiCuentaNoExiste() throws Exception {
            when(accountService.obtenerCuenta("ACC-999"))
                    .thenThrow(new IllegalArgumentException("Cuenta no encontrada: ACC-999"));

            mockMvc.perform(get("/api/cuentas/ACC-999"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Cuenta no encontrada: ACC-999"));
        }
    }
}