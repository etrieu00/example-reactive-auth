package com.etrieu00.examplereactiveauth.controller;

import com.etrieu00.examplereactiveauth.api.UserAPI;
import com.etrieu00.examplereactiveauth.request.ProfileUpdateRequest;
import com.etrieu00.examplereactiveauth.response.ProfileResponse;
import com.etrieu00.examplereactiveauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController implements UserAPI {

  private final UserService service;

  @Override
  public Mono<ProfileResponse> userProfileDetails() {
    return service.profileInformation()
      .timeout(Duration.ofSeconds(5))
      .retry(2);
  }

  @Override
  public Mono<ProfileResponse> updateUserProfile(ProfileUpdateRequest body) {
    return service.updateUserProfile(body)
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
