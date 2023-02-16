package com.mate.models.responses.edge;

import com.mate.models.entities.EdgeMaster;
import com.mate.models.responses.BaseApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEdgeResponse extends BaseApiResponse {
  private EdgeMaster edgeMaster;
}
