package com.etrieu00.examplereactiveauth.repository;

import com.etrieu00.examplereactiveauth.entity.AppProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AppProfileRepository extends ReactiveCrudRepository<AppProfile, Long> {
  Mono<AppProfile> findByUuid(String uuid);
}
