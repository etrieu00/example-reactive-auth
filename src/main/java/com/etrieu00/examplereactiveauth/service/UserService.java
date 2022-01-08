package com.etrieu00.examplereactiveauth.service;

import com.etrieu00.examplereactiveauth.exception.UserNotFoundException;
import com.etrieu00.examplereactiveauth.repository.AppProfileRepository;
import com.etrieu00.examplereactiveauth.request.ProfileUpdateRequest;
import com.etrieu00.examplereactiveauth.response.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.etrieu00.examplereactiveauth.util.AuthUtil.getUuid;

@Service
@RequiredArgsConstructor
public class UserService {

  private final AppProfileRepository appProfileRepository;

  public Mono<ProfileResponse> profileInformation() {
    return getUuid()
      .flatMap(appProfileRepository::findByUuid)
      .map(ProfileResponse::new)
      .switchIfEmpty(Mono.defer(() -> Mono.error(new UserNotFoundException("The user does not exist."))));
  }

  public Mono<ProfileResponse> updateUserProfile(ProfileUpdateRequest body) {
    return getUuid()
      .flatMap(appProfileRepository::findByUuid)
      .switchIfEmpty(Mono.defer(() -> Mono.error(new UserNotFoundException("The user does not exist."))))
      .flatMap(profile -> appProfileRepository.save(profile
        .setFirstName(body.getFirstname() != null ? body.getFirstname() : profile.getFirstName())
        .setLastName(body.getLastname() != null ? body.getLastname() : profile.getLastName())
        .setPhoneNumber(body.getPhone() != null ? body.getPhone() : profile.getPhoneNumber())
        .setGender(body.getGender() != null ? body.getGender() : profile.getGender())
        .setDateOfBirth(body.getDob() != null ? body.getDob() : profile.getDateOfBirth())))
      .timeout(Duration.ofMillis(500))
      .map(ProfileResponse::new);
  }
}
