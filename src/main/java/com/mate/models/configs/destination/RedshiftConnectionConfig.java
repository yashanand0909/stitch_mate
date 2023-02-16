package com.mate.models.configs.destination;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.NotNull;
import liquibase.repackaged.org.apache.commons.lang3.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedshiftConnectionConfig extends DestinationConfig {

  @NotNull private String idColumns;
  @NotNull private String tableName;
  @NotNull private String schema;

  public void setTableName(String tableName) {
    this.tableName = StringUtils.lowerCase(tableName);
  }
}
