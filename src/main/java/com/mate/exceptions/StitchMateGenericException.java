package com.mate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class StitchMateGenericException extends RuntimeException {
  public StitchMateGenericException(String message) {
    super(message);
  }
}
