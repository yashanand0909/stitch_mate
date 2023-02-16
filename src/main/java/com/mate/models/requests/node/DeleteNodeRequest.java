package com.mate.models.requests.node;

import com.dream11.models.requests.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteNodeRequest extends BaseRequest {
  private Long nodeId;
}
