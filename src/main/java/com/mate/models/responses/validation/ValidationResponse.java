package com.mate.models.responses.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResponse {

  public ValidationResponse(Boolean isValid) {
    this.isValid = isValid;
  }

  private Boolean isValid;
  private String comment;
}
