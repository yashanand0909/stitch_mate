package com.mate.models.requests.node;

import com.dream11.models.configs.destination.DestinationConfig;
import com.dream11.models.configs.queryparams.ParamConfig;
import com.dream11.models.enums.Source;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseNodeRequest {
  @NotNull Long jobId;
  @NotNull String jobName;
  Source source;
  String nodeName;
  String query;
  ParamConfig parameterConfig;
  DestinationConfig destinationConfig;
  @NotNull Map<String, Object> metadata;
}
