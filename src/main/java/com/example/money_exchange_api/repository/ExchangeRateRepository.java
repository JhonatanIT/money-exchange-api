package com.example.money_exchange_api.repository;

import com.example.money_exchange_api.model.entity.ExchangeRate;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ExchangeRateRepository extends R2dbcRepository<ExchangeRate, Long> {
    Mono<ExchangeRate> findBySourceCurrencyAndTargetCurrency(String sourceCurrency, String targetCurrency);

    @Query("UPDATE exchange_rates SET rate = :rate, updated_at = CURRENT_TIMESTAMP, updated_by = :username " +
            "WHERE source_currency = :sourceCurrency AND target_currency = :targetCurrency")
    Mono<Integer> updateRate(String sourceCurrency, String targetCurrency, Double rate, String username);
}