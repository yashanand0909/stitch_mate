package com.mate.models.responses.job;

import com.mate.models.responses.BaseApiResponse;
import com.mate.models.responses.airflow.DagRun;
import java.util.List;
import lombok.Data;

@Data
public class JobRunsResponse extends BaseApiResponse {
  private List<DagRun> jobRuns;
}
