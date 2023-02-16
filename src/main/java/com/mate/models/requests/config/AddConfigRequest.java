package com.mate.models.requests.config;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddConfigRequest {
  @NotNull private String configKey;
  @NotNull private String configValue;
  private String createdBy;
}
