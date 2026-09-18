package com.walletapi.walletapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CrearCuentaRequest(
        @NotBlank String accountNumber,
        @NotNull @PositiveOrZero BigDecimal saldoInicial
) {}