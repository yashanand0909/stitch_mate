package com.mate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class StitchMateBadRequestException extends RuntimeException {
  public StitchMateBadRequestException(String message) {
    super(message);
  }
}
