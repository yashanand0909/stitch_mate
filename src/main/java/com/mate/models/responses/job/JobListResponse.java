package com.mate.models.responses.job;

import com.mate.models.entities.DagRuns;
import com.mate.models.entities.JobMaster;
import java.util.List;
import lombok.Data;

@Data
public class JobListResponse {
  private JobMaster jobMaster;
  private List<DagRuns> dagRuns;
  private String nextDagRun;
}
