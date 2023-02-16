package com.mate.models.configs.queryparams;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mate.models.enums.ParameterType;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    visible = true,
    include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
  @JsonSubTypes.Type(value = DateConfigSchema.class, name = "DATE"),
  @JsonSubTypes.Type(value = RoundIdConfigSchema.class, name = "ROUNDID"),
})
public class Parameter implements Serializable {

  @NotNull private ParameterType type;
  @NotNull private String parameterName;
}
