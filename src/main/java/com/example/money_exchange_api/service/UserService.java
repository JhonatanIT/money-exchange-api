package com.example.money_exchange_api.service;

import com.example.money_exchange_api.model.dto.AuthRequest;
import com.example.money_exchange_api.model.dto.AuthResponse;
import com.example.money_exchange_api.model.entity.User;
import com.example.money_exchange_api.repository.UserRepository;
import com.example.money_exchange_api.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username).switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username))).map(user -> new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))));
    }

    public Mono<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Mono<AuthResponse> authenticate(AuthRequest authRequest) {
        return userRepository.findByUsername(authRequest.getUsername())
                .filter(user -> passwordEncoder.matches(authRequest.getPassword(), user.getPassword()))
                .map(user -> {
                    UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
                    String token = jwtUtil.generateToken(userDetails);
                    return new AuthResponse(token, user.getUsername(), user.getRole());
                }).switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")));
    }

    public Mono<AuthResponse> createUser(AuthRequest request) {
        return userRepository.findByUsername(request.getUsername()).doOnNext(System.out::println).flatMap(existingUser -> Mono.<AuthResponse>error(new RuntimeException("Username already exists"))).switchIfEmpty(Mono.defer(() -> {
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            newUser.setRole("ADMIN");

            return userRepository.save(newUser).flatMap(savedUser -> {
                UserDetails userDetails = new org.springframework.security.core.userdetails.User(savedUser.getUsername(), savedUser.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + savedUser.getRole())));
                String token = jwtUtil.generateToken(userDetails);
                return Mono.just(new AuthResponse(token, savedUser.getUsername(), savedUser.getRole()));
            });
        }));
    }
}