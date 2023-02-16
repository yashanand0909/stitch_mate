package com.mate.models.responses.node;

import com.mate.models.responses.BaseApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteNodeResponse extends BaseApiResponse {
  private long deletedNodeId;
}
