package com.mate.models.requests.edge;

import com.mate.models.requests.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEdgeRequest extends BaseRequest {
  private Long fromNode;
  private Long toNode;
}
