package com.example.money_exchange_api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@SpringBootApplication
@EnableR2dbcAuditing
@OpenAPIDefinition(
		info = @Info(
				title = "Money Exchange API",
				version = "1.0",
				description = "Money Exchange API"
		)
)
public class MoneyExchangeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneyExchangeApiApplication.class, args);
	}

}
