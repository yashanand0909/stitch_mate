package com.mate.models.requests.edge;

import com.dream11.models.requests.BaseRequest;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteEdgeRequest extends BaseRequest {
  @NotNull private Long edgeId;
}
