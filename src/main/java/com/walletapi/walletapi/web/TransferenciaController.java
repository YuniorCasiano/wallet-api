package com.walletapi.walletapi.web;

import com.walletapi.walletapi.application.TransferenciaService;
import com.walletapi.walletapi.domain.Transaction;
import com.walletapi.walletapi.web.dto.TransactionResponse;
import com.walletapi.walletapi.web.dto.TransferenciaRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transferencias")
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    public TransferenciaController(TransferenciaService transferenciaService) {
        this.transferenciaService = transferenciaService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> transferir(@Valid @RequestBody TransferenciaRequest request) {
        Transaction transaction = transferenciaService.transferir(
                request.cuentaOrigen(), request.cuentaDestino(), request.monto());
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(transaction));
    }
}