package com.etrieu00.examplereactiveauth.service;

import com.etrieu00.examplereactiveauth.entity.AppProfile;
import com.etrieu00.examplereactiveauth.entity.AppUser;
import com.etrieu00.examplereactiveauth.exception.ExistingUserException;
import com.etrieu00.examplereactiveauth.exception.UnauthorizedAccessException;
import com.etrieu00.examplereactiveauth.exception.UserNotFoundException;
import com.etrieu00.examplereactiveauth.repository.AppProfileRepository;
import com.etrieu00.examplereactiveauth.repository.AppUserRepository;
import com.etrieu00.examplereactiveauth.request.CredentialUpdateRequest;
import com.etrieu00.examplereactiveauth.request.LoginRequest;
import com.etrieu00.examplereactiveauth.request.SignUpRequest;
import com.etrieu00.examplereactiveauth.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.UUID;

import static com.etrieu00.examplereactiveauth.util.AuthUtil.getUuid;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AppUserRepository appUserRepository;
  private final AppProfileRepository appProfileRepository;
  private final JwtProviderService jwtProviderService;


  private static final Integer SALT = 12;

  public Mono<AuthResponse> createUserAccount(SignUpRequest body) {
    return Mono.just(body)
      .flatMap(req -> appUserRepository.findAllByUserEmail(req.getUsername()))
      .flatMap(user -> Mono.error(new ExistingUserException("The user already exists " + user.getUserEmail())))
      .switchIfEmpty(Mono.defer(() -> Mono.just(body)
        .flatMap(this::createNewUser)
        .flatMap(this::createNewProfile)
        .flatMap(this::createResponse)))
      .cast(AuthResponse.class);
  }

  private Mono<Tuple2<SignUpRequest, AppUser>> createNewUser(SignUpRequest request) {
    return Mono.zip(Mono.just(request), appUserRepository.save(AppUser.build(
        builder -> builder
          .setUuid(UUID.randomUUID().toString())
          .setUserEmail(request.getUsername())
          .setUserPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(SALT)))
          .setUserRole("USER")))
      .timeout(Duration.ofMillis(500))
      .doOnError(error -> Mono.error(new RuntimeException(error.getMessage()))));
  }

  private Mono<AppUser> createNewProfile(Tuple2<SignUpRequest, AppUser> tuple) {
    return appProfileRepository.save(AppProfile.build(
        builder -> builder
          .setUuid(tuple.getT2().getUuid())
          .setFirstName(tuple.getT1().getFirstname())
          .setLastName(tuple.getT1().getLastname())))
      .timeout(Duration.ofMillis(500))
      .doOnError(error -> Mono.error(new RuntimeException(error.getMessage())))
      .map(__ -> tuple.getT2());
  }

  private Mono<AuthResponse> createResponse(AppUser user) {
    return Mono.just(jwtProviderService.generateRefreshToken(user))
      .map(refresh -> new AuthResponse(refresh, jwtProviderService.generateAccessToken(refresh)))
      .doOnError(error -> Mono.error(new RuntimeException(error.getMessage())));
  }

  public Mono<AuthResponse> loginToUserAccount(LoginRequest body) {
    return Mono.just(body)
      .flatMap(req -> appUserRepository.findAllByUserEmail(req.getUsername()))
      .filter(user ->  BCrypt.checkpw(body.getPassword(),user.getUserPassword()))
      .switchIfEmpty(Mono.defer(() ->  Mono.error(new UserNotFoundException("The user does not exist."))))
      .flatMap(this::createResponse);
  }

  public Mono<AuthResponse> generateTokens(String token) {
    return Mono.just(new AuthResponse(
      jwtProviderService.reissueRefreshToken(token),
      jwtProviderService.generateAccessToken(token)
    ));
  }

  public Mono<AuthResponse> updateCredentials(CredentialUpdateRequest body, String type) {
    switch (type) {
      case "password":
        return getUuid()
          .flatMap(appUserRepository::findByUuid)
          .filter(user -> BCrypt.checkpw(body.getOldPassword(),user.getUserPassword()))
          .switchIfEmpty(Mono.defer(() ->  Mono.error(new UnauthorizedAccessException("The password is incorrect."))))
          .map(user -> user.setUserPassword(BCrypt.hashpw(body.getNewPassword(), BCrypt.gensalt(SALT))))
          .flatMap(appUserRepository::save)
          .doOnError(error -> Mono.error(new RuntimeException(error.getMessage())))
          .flatMap(this::createResponse);
      case "username":
        return getUuid()
          .flatMap(appUserRepository::findByUuid)
          .filter(user -> BCrypt.checkpw(body.getOldPassword(),user.getUserPassword()))
          .switchIfEmpty(Mono.defer(() ->  Mono.error(new UnauthorizedAccessException("The password is incorrect."))))
          .map(user -> user.setUserEmail(body.getUsername()))
          .flatMap(appUserRepository::save)
          .doOnError(error -> Mono.error(new RuntimeException(error.getMessage())))
          .flatMap(this::createResponse);
      default:
        return Mono.error(new IllegalArgumentException("Invalid param"));
    }
  }
}
