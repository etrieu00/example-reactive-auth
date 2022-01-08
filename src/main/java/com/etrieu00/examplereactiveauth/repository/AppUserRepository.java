package com.etrieu00.examplereactiveauth.repository;

import com.etrieu00.examplereactiveauth.entity.AppUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AppUserRepository extends ReactiveCrudRepository<AppUser, Long> {
  Mono<AppUser> findAllByUserEmail(String email);
  Mono<AppUser> findByUuid(String uuid);
}
