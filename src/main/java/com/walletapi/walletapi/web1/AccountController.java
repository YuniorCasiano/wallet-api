package com.walletapi.walletapi.web;

import com.walletapi.walletapi.application.AccountService;
import com.walletapi.walletapi.domain.Account;
import com.walletapi.walletapi.web.dto.AccountResponse;
import com.walletapi.walletapi.web.dto.CrearCuentaRequest;
import com.walletapi.walletapi.web.dto.MontoRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cuentas")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> crearCuenta(@Valid @RequestBody CrearCuentaRequest request) {
        Account account = accountService.crearCuenta(request.accountNumber(), request.saldoInicial());
        return ResponseEntity.status(HttpStatus.CREATED).body(AccountResponse.from(account));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> obtenerCuenta(@PathVariable String accountNumber) {
        Account account = accountService.obtenerCuenta(accountNumber);
        return ResponseEntity.ok(AccountResponse.from(account));
    }

    @PostMapping("/{accountNumber}/depositos")
    public ResponseEntity<AccountResponse> depositar(@PathVariable String accountNumber,
                                                     @Valid @RequestBody MontoRequest request) {
        Account account = accountService.depositar(accountNumber, request.monto());
        return ResponseEntity.ok(AccountResponse.from(account));
    }

    @PostMapping("/{accountNumber}/retiros")
    public ResponseEntity<AccountResponse> retirar(@PathVariable String accountNumber,
                                                   @Valid @RequestBody MontoRequest request) {
        Account account = accountService.retirar(accountNumber, request.monto());
        return ResponseEntity.ok(AccountResponse.from(account));
    }
}