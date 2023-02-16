package com.mate.models.responses.node;

import com.mate.models.responses.BaseApiResponse;
import java.util.List;
import lombok.Data;

@Data
public class NodeRunLogsResponse extends BaseApiResponse {

  private List<NodeRunLog> data;
}
