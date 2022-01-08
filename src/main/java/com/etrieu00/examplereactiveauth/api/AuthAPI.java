package com.etrieu00.examplereactiveauth.api;

import com.etrieu00.examplereactiveauth.exception.ExistingUserException;
import com.etrieu00.examplereactiveauth.exception.UnauthorizedAccessException;
import com.etrieu00.examplereactiveauth.exception.UserNotFoundException;
import com.etrieu00.examplereactiveauth.request.CredentialUpdateRequest;
import com.etrieu00.examplereactiveauth.request.LoginRequest;
import com.etrieu00.examplereactiveauth.request.SignUpRequest;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@Validated
@RequestMapping(value = "app/api/v1/auth",
  produces = MediaType.APPLICATION_JSON_VALUE)
public interface AuthAPI {

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
  Object createUserAccount(@RequestBody @Valid final SignUpRequest body);

  @ResponseStatus(HttpStatus.OK)
  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
  Object loginUserAccount(@RequestBody @Valid final LoginRequest body);

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/refresh")
  Object issueAccessToken(@RequestParam(name = "token") final String token);

  @ResponseStatus(HttpStatus.ACCEPTED)
  @PutMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
  Object updateCredentials(@RequestBody @Valid final CredentialUpdateRequest body,
                           @RequestParam(value = "type", defaultValue = "password") final String type);

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler({
    ExistingUserException.class,
    UserNotFoundException.class})
  Object handleConflicts(Exception e);

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler({
    ExpiredJwtException.class,
    UnauthorizedAccessException.class
  })
  Object handleUnauthorized(Exception e);
}
