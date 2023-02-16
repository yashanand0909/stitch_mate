package com.mate.models.responses.airflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DagRunsResponse {
  @JsonProperty("dag_runs")
  private List<DagRun> dagRuns;
}
