package com.mate.models.configs.queryparams;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoundIdParameterConfig extends Parameter {

  @NotNull private int roundId;
}
