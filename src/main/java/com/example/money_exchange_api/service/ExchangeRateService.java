package com.example.money_exchange_api.service;

import com.example.money_exchange_api.model.dto.ExchangeRateRequest;
import com.example.money_exchange_api.model.dto.ExchangeRateResponse;
import com.example.money_exchange_api.model.entity.ExchangeRate;
import com.example.money_exchange_api.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    public Flux<ExchangeRateResponse> getAllExchangeRates() {
        return exchangeRateRepository.findAll()
                .doOnNext(System.out::println)
                .map(this::mapToResponse);
    }

    public Mono<ExchangeRateResponse> getExchangeRateBySourceAndTarget(String sourceCurrency, String targetCurrency) {
        return exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(sourceCurrency, targetCurrency)
                .map(this::mapToResponse)
                .switchIfEmpty(Mono.error(new RuntimeException("Exchange rate not found for the given currency pair")));
    }

    public Mono<ExchangeRateResponse> createExchangeRate(ExchangeRateRequest request, String username) {
        return exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(
                        request.getSourceCurrency(), request.getTargetCurrency())
                .flatMap(existingRate -> Mono.error(new RuntimeException("Exchange rate already exists for the given currency pair")))
                .switchIfEmpty(Mono.defer(() -> {
                    ExchangeRate exchangeRate = ExchangeRate.builder()
                            .sourceCurrency(request.getSourceCurrency())
                            .targetCurrency(request.getTargetCurrency())
                            .rate(request.getRate())
                            .createdBy(username)
                            .build();
                    System.out.println("Creating new exchange rate: " + exchangeRate);
                    return exchangeRateRepository.save(exchangeRate);
                }))
                .map((Object exchangeRate) -> mapToResponse((ExchangeRate) exchangeRate));
    }

    public Mono<ExchangeRateResponse> updateExchangeRate(String sourceCurrency, String targetCurrency,
                                                         ExchangeRateRequest request, String username) {
        return exchangeRateRepository.findBySourceCurrencyAndTargetCurrency(sourceCurrency, targetCurrency)
                .switchIfEmpty(Mono.error(new RuntimeException("Exchange rate not found for the given currency pair")))
                .flatMap(existingRate -> {
                    existingRate.setRate(request.getRate());
                    existingRate.setUpdatedBy(username);
                    return exchangeRateRepository.save(existingRate);
                })
                .map(this::mapToResponse);
    }

    private ExchangeRateResponse mapToResponse(ExchangeRate exchangeRate) {
        return ExchangeRateResponse.builder()
                .id(exchangeRate.getId())
                .sourceCurrency(exchangeRate.getSourceCurrency())
                .targetCurrency(exchangeRate.getTargetCurrency())
                .rate(exchangeRate.getRate())
                .createdAt(exchangeRate.getCreatedAt())
                .updatedAt(exchangeRate.getUpdatedAt())
                .createdBy(exchangeRate.getCreatedBy())
                .updatedBy(exchangeRate.getUpdatedBy())
                .build();
    }
}