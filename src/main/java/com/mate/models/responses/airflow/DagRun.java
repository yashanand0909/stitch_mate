package com.mate.models.responses.airflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DagRun {
  @JsonProperty("dag_id")
  private String dagId;

  @JsonProperty("dag_run_id")
  private String dagRunId;

  @JsonProperty("start_date")
  private String startDate;

  @JsonProperty("end_date")
  private String endDate;

  @JsonProperty("logical_date")
  private String scheduleDate;

  @JsonProperty("state")
  private String status;
}
