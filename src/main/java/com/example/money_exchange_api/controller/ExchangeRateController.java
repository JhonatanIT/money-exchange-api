package com.example.money_exchange_api.controller;

import com.example.money_exchange_api.model.dto.ExchangeRateRequest;
import com.example.money_exchange_api.model.dto.ExchangeRateResponse;
import com.example.money_exchange_api.model.dto.ExchangeRequest;
import com.example.money_exchange_api.model.entity.ExchangeTransaction;
import com.example.money_exchange_api.service.ExchangeRateService;
import com.example.money_exchange_api.service.ExchangeTransactionService;
import com.example.money_exchange_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Exchange Rates", description = "Exchange Rate API")
@SecurityRequirement(name = "Bearer Authentication")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;
    private final ExchangeTransactionService exchangeTransactionService;
    private final UserService userService;

    @GetMapping("/exchange-rates")
    @Operation(summary = "Get all exchange rates", description = "Returns all available exchange rates")
    public Flux<ExchangeRateResponse> getAllExchangeRates() {
        return exchangeRateService.getAllExchangeRates();
    }

    @GetMapping("/exchange-rates/{sourceCurrency}/{targetCurrency}")
    @Operation(summary = "Get specific exchange rate", description = "Returns the exchange rate for a specific currency pair")
    public Mono<ResponseEntity<ExchangeRateResponse>> getExchangeRate(
            @PathVariable String sourceCurrency,
            @PathVariable String targetCurrency) {
        return exchangeRateService.getExchangeRateBySourceAndTarget(sourceCurrency, targetCurrency)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping("/exchange-rates")
    @Operation(summary = "Create exchange rate", description = "Creates a new exchange rate")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<ExchangeRateResponse>> createExchangeRate(
            @Valid @RequestBody ExchangeRateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return exchangeRateService.createExchangeRate(request, userDetails.getUsername())
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @PutMapping("/exchange-rates/{sourceCurrency}/{targetCurrency}")
    @Operation(summary = "Update exchange rate", description = "Updates an existing exchange rate")
    public Mono<ResponseEntity<ExchangeRateResponse>> updateExchangeRate(
            @PathVariable String sourceCurrency,
            @PathVariable String targetCurrency,
            @Valid @RequestBody ExchangeRateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return exchangeRateService.updateExchangeRate(sourceCurrency, targetCurrency, request, userDetails.getUsername())
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping("/exchange")
    @Operation(summary = "Perform currency exchange", description = "Performs a currency exchange and records the transaction")
    public Mono<ResponseEntity<ExchangeTransaction>> performExchange(
            @Valid @RequestBody ExchangeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return userService.findUserByUsername(userDetails.getUsername())
                .flatMap(user -> exchangeTransactionService.performExchange(request, user))
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get user transactions", description = "Returns all transactions for the authenticated user")
    public Flux<ExchangeTransaction> getUserTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        return exchangeTransactionService.getTransactionsByUsername(userDetails.getUsername());
    }

    @GetMapping("/transactions/all")
    @Operation(summary = "Get all transactions", description = "Returns all transactions (Admin only)")
    public Flux<ExchangeTransaction> getAllTransactions() {
        return exchangeTransactionService.getAllTransactions();
    }
}