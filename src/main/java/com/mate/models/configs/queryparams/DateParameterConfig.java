package com.mate.models.configs.queryparams;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mate.models.enums.TimeDelay;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DateParameterConfig {

  @NotNull private String dateFormat;
  @NotNull private TimeDelay delayType;
  @NotNull private int delayNumber;
}
