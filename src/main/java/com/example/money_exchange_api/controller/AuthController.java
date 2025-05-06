package com.example.money_exchange_api.controller;

import com.example.money_exchange_api.model.dto.AuthRequest;
import com.example.money_exchange_api.model.dto.AuthResponse;
import com.example.money_exchange_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticate a user and return a JWT token")
    public Mono<ResponseEntity<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        return userService.authenticate(request)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(401).build()));
    }

    @PostMapping("/signup")
    @Operation(summary = "Register user", description = "Register a new user and return a JWT token")
    public Mono<ResponseEntity<AuthResponse>> signup(@Valid @RequestBody AuthRequest request) {
        return userService.createUser(request)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }
}