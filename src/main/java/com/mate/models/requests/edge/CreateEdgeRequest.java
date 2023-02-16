package com.mate.models.requests.edge;

import com.dream11.models.requests.BaseRequest;
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
