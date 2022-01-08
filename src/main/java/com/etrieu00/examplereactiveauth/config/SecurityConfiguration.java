package com.etrieu00.examplereactiveauth.config;

import com.etrieu00.examplereactiveauth.service.JwtProviderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity,
                                                       AuthenticationWebFilter authenticationWebFilter) {
    return configurePaths(httpSecurity)
      .and()
      .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
      .httpBasic().disable()
      .csrf().disable()
      .formLogin().disable()
      .logout().disable()
      .build();
  }

  @Bean
  public AuthenticationWebFilter customAuthenticationWebFilter(JwtProviderService provider) {
    var filter = new AuthenticationWebFilter(reactiveAuthenticationManager(provider));
    filter.setServerAuthenticationConverter(serverAuthenticationConverter());
    return filter;
  }

  private ServerHttpSecurity.AuthorizeExchangeSpec configurePaths(ServerHttpSecurity exchange) {
    return exchange
      .authorizeExchange()
      .pathMatchers("/app/api/v1/auth/signup").permitAll()
      .pathMatchers("/app/api/v1/auth/login").permitAll()
      .pathMatchers("/app/api/v1/auth/refresh").permitAll()
      .pathMatchers("/app/api/v1/auth/user","/app/api/v1/user/**")
      .authenticated();
  }

  public ReactiveAuthenticationManager reactiveAuthenticationManager(JwtProviderService provider) {
    return authentication -> Mono.just(authentication)
      .map(auth -> provider.parseAccessToken(auth.getCredentials().toString()))
      .onErrorResume(error -> Mono.empty())
      .map(jwt -> new UsernamePasswordAuthenticationToken(
        jwt.getBody().get("id", String.class),
        authentication.getCredentials(),
        List.of(new SimpleGrantedAuthority(jwt.getBody().get("role", String.class)))));
  }

  public ServerAuthenticationConverter serverAuthenticationConverter() {
    return exchange -> Mono.justOrEmpty(exchange)
      .flatMap(data ->
        Mono.justOrEmpty(data.getRequest().getHeaders().get("Authorization")))
      .filter(bearer -> !bearer.isEmpty())
      .map(token -> token.get(0).split(" ")[1])
      .map(principle -> new UsernamePasswordAuthenticationToken(principle, principle));
  }
}
