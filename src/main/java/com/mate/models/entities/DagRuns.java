package com.mate.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import liquibase.repackaged.org.apache.commons.lang3.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "dag_run", catalog = "airflow")
public class DagRuns {

  @Id private long id;

  @Column(name = "dag_id")
  @JsonProperty("dag_id")
  private String dagId;

  @Column(name = "run_id")
  @JsonProperty("dag_run_id")
  private String runId;

  @Column(name = "execution_date")
  @JsonProperty("logical_date")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime executionDate;

  @Column(name = "start_date")
  @JsonProperty("start_date")
  private String startDate;

  @Column(name = "end_date")
  @JsonProperty("end_date")
  private String endDate;

  @Column(name = "state", insertable = false, updatable = false)
  @JsonProperty("state")
  private String status;

  public String getStatus() {
    return StringUtils.upperCase(status);
  }

  public void setStatus(String status) {
    this.status = StringUtils.lowerCase(status);
  }

  public String getStartDate() {
    return startDate + " UTC";
  }

  public String getEndDate() {
    return endDate + " UTC";
  }
}
