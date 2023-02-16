package com.mate.models.responses.edge;

import com.dream11.models.responses.BaseApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteEdgeResponse extends BaseApiResponse {
  private long deletedEdgeId;
}
