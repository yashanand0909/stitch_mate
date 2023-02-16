package com.mate.models.responses;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class ErrorResponse {
  // Error code
  private Integer errorCode;
  // Specific errors in API request processing
  private List<String> errors;
  // General error message about nature of error
  private String message;
  // time the error happened
  private String timestamp;

  /**
   * Constructor for error response.
   *
   * @param message The message to be attached to the error response.
   * @param errors The error that needs to be attached.
   * @param errorCode The error code for the given errors.
   */
  public ErrorResponse(String message, List<String> errors, Integer errorCode) {
    super();
    this.message = message;
    this.errors = errors;
    this.timestamp = LocalDateTime.now().toString();
    this.errorCode = errorCode;
  }
}
