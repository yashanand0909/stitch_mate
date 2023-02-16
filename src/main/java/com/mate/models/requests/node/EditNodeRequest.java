package com.mate.models.requests.node;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditNodeRequest extends BaseNodeRequest {

  @NotNull Long nodeId;
}
