package com.mate.models.responses.node;

import com.mate.models.entities.NodeMaster;
import com.mate.models.responses.BaseApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NodeResponse extends BaseApiResponse {
  private NodeMaster nodeMaster;
}
