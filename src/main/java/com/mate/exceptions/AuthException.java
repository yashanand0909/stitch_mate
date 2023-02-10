package com.mate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "User Already Exists")
public class AuthException extends RuntimeException {
  public AuthException(String message) {
    super(message);
  }
}
