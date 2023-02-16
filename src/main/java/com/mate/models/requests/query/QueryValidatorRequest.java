package com.mate.models.requests.query;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QueryValidatorRequest {

  @NotNull String query;
  private Long jobId; // Remove this in future version as this is not being used anywhere
}
