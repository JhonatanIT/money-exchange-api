package com.example.money_exchange_api.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("exchange_transactions")
public class ExchangeTransaction {

    @Id
    private Long id;

    @Column("source_currency")
    private String sourceCurrency;

    @Column("target_currency")
    private String targetCurrency;

    @Column("source_amount")
    private BigDecimal sourceAmount;

    @Column("target_amount")
    private BigDecimal targetAmount;

    @Column("exchange_rate")
    private BigDecimal exchangeRate;

    @Column("user_id")
    private Long userId;

    @Column("username")
    private String username;

    @Column("transaction_date")
    private LocalDateTime transactionDate;
}