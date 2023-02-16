package com.mate.models.configs.queryparams;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParamConfig {

  @NotNull private List<Parameter> parametersList;
}
