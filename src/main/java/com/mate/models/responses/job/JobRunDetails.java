package com.mate.models.responses.job;

import com.mate.models.entities.JobMaster;
import com.mate.repositories.projections.EdgeList;
import com.mate.repositories.projections.NodeRunsList;
import java.util.List;
import lombok.Data;

@Data
public class JobRunDetails {
  private JobMaster job;
  private List<EdgeList> edges;
  private List<NodeRunsList> nodes;
}
