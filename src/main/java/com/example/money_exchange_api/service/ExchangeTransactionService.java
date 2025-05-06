package com.example.money_exchange_api.service;

import com.example.money_exchange_api.model.dto.ExchangeRequest;
import com.example.money_exchange_api.model.entity.ExchangeTransaction;
import com.example.money_exchange_api.model.entity.User;
import com.example.money_exchange_api.repository.ExchangeRateRepository;
import com.example.money_exchange_api.repository.ExchangeTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExchangeTransactionService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeTransactionRepository exchangeTransactionRepository;

    public Mono<ExchangeTransaction> performExchange(ExchangeRequest request, User user) {
        return exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(
                        request.getSourceCurrency(), request.getTargetCurrency())
                .switchIfEmpty(Mono.error(new RuntimeException("Exchange rate not found for the given currency pair")))
                .flatMap(exchangeRate -> {
                    BigDecimal targetAmount = request.getAmount().multiply(exchangeRate.getRate())
                            .setScale(2, RoundingMode.HALF_UP);

                    ExchangeTransaction transaction = ExchangeTransaction.builder()
                            .sourceCurrency(request.getSourceCurrency())
                            .targetCurrency(request.getTargetCurrency())
                            .sourceAmount(request.getAmount())
                            .targetAmount(targetAmount)
                            .exchangeRate(exchangeRate.getRate())
                            .userId(user.getId())
                            .username(user.getUsername())
                            .transactionDate(LocalDateTime.now())
                            .build();

                    return exchangeTransactionRepository.save(transaction);
                });
    }

    public Flux<ExchangeTransaction> getTransactionsByUsername(String username) {
        return exchangeTransactionRepository.findByUsername(username);
    }

    public Flux<ExchangeTransaction> getAllTransactions() {
        return exchangeTransactionRepository.findAll();
    }
}