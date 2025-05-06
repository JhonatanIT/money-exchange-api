package com.example.money_exchange_api.repository;

import com.example.money_exchange_api.model.entity.ExchangeTransaction;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ExchangeTransactionRepository extends R2dbcRepository<ExchangeTransaction, Long> {
    Flux<ExchangeTransaction> findByUserId(Long userId);
    Flux<ExchangeTransaction> findByUsername(String username);
}