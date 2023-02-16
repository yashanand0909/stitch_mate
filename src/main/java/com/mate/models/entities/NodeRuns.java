package com.mate.models.entities;

import com.dream11.models.converter.HashMapConverter;
import com.dream11.models.enums.NodeStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "node_runs")
public class NodeRuns {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nodes_run_id")
  private long nodeRunId;

  @Column(name = "updated_by")
  private String updatedBy;

  @Column(name = "job_id", nullable = false)
  private long jobId;

  @Column(name = "dag_run_id", nullable = false)
  private String dagRunId;

  @Column(name = "node_id", nullable = false)
  private long nodeId;

  @Column(name = "node_name", nullable = false)
  private String nodeName;

  @Column(name = "executed_query", nullable = false)
  private String executedQuery;

  @Column(name = "execution_summary", nullable = false)
  private String executionSummary;

  @Column(name = "destination", nullable = false)
  private String destination;

  @Enumerated(EnumType.STRING)
  @Column(name = "node_status", nullable = false)
  private NodeStatus nodeStatus;

  @Column(name = "metadata")
  @Convert(converter = HashMapConverter.class)
  private Map<String, Object> metadata;

  @CreationTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "recupdated_at", nullable = false)
  private LocalDateTime recupdatedAt;
}
