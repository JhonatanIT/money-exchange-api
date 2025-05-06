package com.example.money_exchange_api.exception;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  WebProperties.Resources resources,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer configurer) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageWriters(configurer.getWriters());
        this.setMessageReaders(configurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        Map<String, Object> errorMap = new HashMap<>();

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "Internal Server Error";

        if (error instanceof CustomException) {
            status = ((CustomException) error).getStatus();
            errorMessage = error.getMessage();
        } else if (error instanceof org.springframework.security.access.AccessDeniedException) {
            status = HttpStatus.FORBIDDEN;
            errorMessage = "Denied access";
        } else if (error instanceof org.springframework.web.server.ResponseStatusException) {
            status = (HttpStatus) ((org.springframework.web.server.ResponseStatusException) error).getStatusCode();
            errorMessage = error.getMessage();
        }

        errorMap.put("status", status.value());
        errorMap.put("error", status.getReasonPhrase());
        errorMap.put("message", errorMessage);
        errorMap.put("path", request.path());

        return ServerResponse
                .status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorMap));
    }
}

@Configuration
class ErrorWebFluxConfiguration {
    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }
}