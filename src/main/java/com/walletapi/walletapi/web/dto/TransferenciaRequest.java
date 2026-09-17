package com.walletapi.walletapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferenciaRequest(
        @NotBlank String cuentaOrigen,
        @NotBlank String cuentaDestino,
        @NotNull @Positive BigDecimal monto
) {}