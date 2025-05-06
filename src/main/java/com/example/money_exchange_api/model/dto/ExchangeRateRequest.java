package com.example.money_exchange_api.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateRequest {

    @NotBlank(message = "The source currency cannot be empty")
    @Size(min = 3, max = 3, message = "The currency code must have exactly 3 characters")
    private String sourceCurrency;

    @NotBlank(message = "The target currency cannot be empty")
    @Size(min = 3, max = 3, message = "The currency code must have exactly 3 characters")
    private String targetCurrency;

    @NotNull(message = "The exchange rate cannot be null")
    @DecimalMin(value = "0.000001", message = "The exchange rate must be greater than zero")
    private BigDecimal rate;
}