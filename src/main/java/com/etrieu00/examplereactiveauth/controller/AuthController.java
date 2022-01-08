package com.etrieu00.examplereactiveauth.controller;

import com.etrieu00.examplereactiveauth.api.AuthAPI;
import com.etrieu00.examplereactiveauth.request.CredentialUpdateRequest;
import com.etrieu00.examplereactiveauth.request.LoginRequest;
import com.etrieu00.examplereactiveauth.request.SignUpRequest;
import com.etrieu00.examplereactiveauth.response.AuthResponse;
import com.etrieu00.examplereactiveauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthAPI {

  private final AuthService service;

  @Override
  public Mono<AuthResponse> createUserAccount(SignUpRequest body) {
    return service.createUserAccount(body)
      .timeout(Duration.ofSeconds(5));
  }

  @Override
  public Mono<AuthResponse> loginUserAccount(LoginRequest body) {
    return service.loginToUserAccount(body)
      .timeout(Duration.ofSeconds(5));
  }

  @Override
  public Mono<AuthResponse> issueAccessToken(String token) {
    return service.generateTokens(token)
      .timeout(Duration.ofSeconds(5))
      .retry(2);
  }

  @Override
  public Mono<AuthResponse> updateCredentials(CredentialUpdateRequest body, String type) {
    return service.updateCredentials(body, type)
      .timeout(Duration.ofSeconds(5));
  }

  @Override
  public Mono<Map<String, ?>> handleConflicts(Exception e) {
    return Mono.just(Map.of("status", 409, "message", e.getMessage()));
  }

  @Override
  public Mono<Map<String, ?>> handleUnauthorized(Exception e) {
    return Mono.just(Map.of("status", 409, "message", e.getMessage()));
  }
}
