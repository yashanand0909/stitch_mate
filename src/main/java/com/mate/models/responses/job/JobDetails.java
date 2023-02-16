package com.mate.models.responses.job;

import com.mate.models.entities.JobMaster;
import com.mate.models.responses.BaseApiResponse;
import com.mate.repositories.projections.EdgeList;
import com.mate.repositories.projections.NodeList;
import com.mate.repositories.projections.TagList;
import java.util.List;
import lombok.Data;

@Data
public class JobDetails extends BaseApiResponse {
  private JobMaster job;
  private List<EdgeList> edges;
  private List<NodeList> nodes;
  private List<TagList> tags;
}
