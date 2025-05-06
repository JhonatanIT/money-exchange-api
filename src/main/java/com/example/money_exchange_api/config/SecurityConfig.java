package com.example.money_exchange_api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;


@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ReactiveUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .exceptionHandling(exceptionHandlingSpec -> {
                    exceptionHandlingSpec
                            .authenticationEntryPoint((exchange, ex) ->
                                    Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                            .accessDeniedHandler((exchange, denied) ->
                                    Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)));
                })
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(authorizeExchangeSpec -> {
                    authorizeExchangeSpec
                            .pathMatchers("/api/auth/**").permitAll()
                            .pathMatchers("/h2-console/**").permitAll()
                            .pathMatchers("/api-docs/**", "/swagger-ui.html", "/webjars/**").permitAll()
                            .pathMatchers(HttpMethod.GET, "/api/exchange-rates/**").permitAll()
                            .pathMatchers(HttpMethod.POST, "/api/exchange").authenticated()
                            .pathMatchers(HttpMethod.POST, "/api/exchange-rates/**").hasRole("ADMIN")
                            .pathMatchers(HttpMethod.PUT, "/api/exchange-rates/**").hasRole("ADMIN")
                            .pathMatchers(HttpMethod.DELETE, "/api/exchange-rates/**").hasRole("ADMIN")
                            .anyExchange().authenticated();
                })
                .build();
    }

//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
//                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
//                .authorizeExchange(authorizeExchangeSpec ->
//                        authorizeExchangeSpec
//                                .anyExchange().permitAll()
//                )
//                .build();
//    }

    private AuthenticationWebFilter jwtAuthenticationFilter() {
        AuthenticationWebFilter webFilter = new AuthenticationWebFilter(authenticationManager());
        webFilter.setServerAuthenticationConverter(new JwtAuthenticationConverter(jwtUtil, userDetailsService));
        webFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));
        return webFilter;
    }
}