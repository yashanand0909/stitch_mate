package com.mate.models.requests.config;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfigRequest {

  private Long configId;
  private String configKey;
  @NotNull private String configValue;
  private String updatedBy;
}
