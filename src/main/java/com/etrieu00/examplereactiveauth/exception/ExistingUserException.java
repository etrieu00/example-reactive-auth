package com.etrieu00.examplereactiveauth.exception;

public class ExistingUserException extends RuntimeException {
  public ExistingUserException(String message) {
    super(message);
  }
}
