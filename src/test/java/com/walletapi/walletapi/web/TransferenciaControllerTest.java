package com.walletapi.walletapi.web;

import com.walletapi.walletapi.application.TransferenciaService;
import com.walletapi.walletapi.domain.Transaction;
import com.walletapi.walletapi.domain.exception.SaldoInsuficienteException;

import static org.mockito.ArgumentMatchers.any;

import com.walletapi.walletapi.web.dto.TransferenciaRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferenciaController.class)
class TransferenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransferenciaService transferenciaService;

    @Nested
    @DisplayName("Transferencia exitosa")
    class TransferenciaExitosa {

        @Test
        @DisplayName("debería devolver 201 con la transacción completada")
        void deberiaTransferirYDevolver201() throws Exception {
            Transaction transaccion = Transaction.transferencia("ACC-001", "ACC-002", new BigDecimal("30.00"));
            transaccion.marcarCompletada();

            when(transferenciaService.transferir(eq("ACC-001"), eq("ACC-002"), eq(new BigDecimal("30.00"))))
                    .thenReturn(transaccion);

            TransferenciaRequest request = new TransferenciaRequest("ACC-001", "ACC-002", new BigDecimal("30.00"));

            mockMvc.perform(post("/api/transferencias")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.cuentaOrigen").value("ACC-001"))
                    .andExpect(jsonPath("$.cuentaDestino").value("ACC-002"))
                    .andExpect(jsonPath("$.status").value("COMPLETADA"));
        }
    }

    @Nested
    @DisplayName("Validación de entrada")
    class ValidacionDeEntrada {

        @Test
        @DisplayName("debería devolver 400 si el monto es negativo, sin llamar al service")
        void deberiaDevolver400SiMontoEsNegativo() throws Exception {
            TransferenciaRequest request = new TransferenciaRequest("ACC-001", "ACC-002", new BigDecimal("-10.00"));

            mockMvc.perform(post("/api/transferencias")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(transferenciaService);
        }

        @Test
        @DisplayName("debería devolver 400 si cuentaDestino viene vacía, sin llamar al service")
        void deberiaDevolver400SiCuentaDestinoEstaVacia() throws Exception {
            TransferenciaRequest request = new TransferenciaRequest("ACC-001", "", new BigDecimal("30.00"));

            mockMvc.perform(post("/api/transferencias")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(transferenciaService);
        }
    }

    @Nested
    @DisplayName("Errores de negocio")
    class ErroresDeNegocio {

        @Test
        @DisplayName("debería devolver 409 si el saldo es insuficiente")
        void deberiaDevolver409SiSaldoEsInsuficiente() throws Exception {
            when(transferenciaService.transferir(eq("ACC-001"), eq("ACC-002"), eq(new BigDecimal("500.00"))))
                    .thenThrow(new SaldoInsuficienteException("ACC-001"));

            TransferenciaRequest request = new TransferenciaRequest("ACC-001", "ACC-002", new BigDecimal("500.00"));

            mockMvc.perform(post("/api/transferencias")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        @DisplayName("debería devolver 400 si la cuenta origen no existe")
        void deberiaDevolver400SiCuentaOrigenNoExiste() throws Exception {
            when(transferenciaService.transferir(eq("ACC-999"), eq("ACC-002"), any()))
                    .thenThrow(new IllegalArgumentException("Cuenta origen no encontrada: ACC-999"));

            TransferenciaRequest request = new TransferenciaRequest("ACC-999", "ACC-002", new BigDecimal("30.00"));

            mockMvc.perform(post("/api/transferencias")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Cuenta origen no encontrada: ACC-999"));
        }
    }
}