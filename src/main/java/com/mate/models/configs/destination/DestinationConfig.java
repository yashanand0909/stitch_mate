package com.mate.models.configs.destination;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mate.models.enums.ConnectorType;
import com.mate.models.enums.Mode;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "connectorType",
    visible = true,
    include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({@JsonSubTypes.Type(value = RedshiftConnectionConfig.class, name = "REDSHIFT")})
public class DestinationConfig implements Serializable {

  @NotNull ConnectorType connectorType;
  @NotNull Mode loadType;
}
