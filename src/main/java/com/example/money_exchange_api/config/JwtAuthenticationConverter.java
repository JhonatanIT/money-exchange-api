package com.example.money_exchange_api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JwtAuthenticationConverter implements ServerAuthenticationConverter {

    private final JwtUtil jwtUtil;
    private final ReactiveUserDetailsService userDetailsService;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange)
                .flatMap(serverWebExchange -> Mono.justOrEmpty(serverWebExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION)))
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .map(authHeader -> authHeader.substring(7))
                .flatMap(token -> {
                    String username = jwtUtil.extractUsername(token);
                    return userDetailsService.findByUsername(username)
                            .filter(userDetails -> jwtUtil.validateToken(token, userDetails))
                            .map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails, "admin", userDetails.getAuthorities()));
                });
    }
}