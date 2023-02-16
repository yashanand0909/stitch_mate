package com.mate.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mate.models.enums.JobStatus;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
@Table(name = "job_master")
public class JobMaster {

  public JobMaster(String jobName, String createdBy, String description) {
    this.jobName = jobName;
    this.createdBy = createdBy;
    this.description = description;
  }

  public JobMaster(
      String jobName,
      String startNode,
      String createdBy,
      String schedule,
      String description,
      boolean backfill,
      LocalDateTime startDate) {
    this.jobName = jobName;
    this.startNode = startNode;
    this.createdBy = createdBy;
    this.schedule = schedule;
    this.description = description;
    this.backfill = backfill;
    this.startDate = startDate;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "job_id")
  private long jobId;

  @Column(name = "job_name", nullable = false)
  private String jobName;

  @Column(name = "start_node")
  private String startNode;

  @Column(name = "created_by", nullable = false)
  private String createdBy;

  @Column(name = "schedule")
  private String schedule;

  @Column(name = "description")
  private String description;

  @Column(name = "backfill")
  private boolean backfill;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "start_date")
  private LocalDateTime startDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "end_date")
  private LocalDateTime endDate;

  @Column(name = "last_execution_id")
  private Integer lastExecutionId;

  @Enumerated(EnumType.STRING)
  @Column(name = "job_status", nullable = false)
  private JobStatus jobStatus;

  @Column(name = "notification_config")
  private String notificationConfig;

  @CreationTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "recupdated_at")
  private LocalDateTime recupdatedAt;

  @JsonIgnore
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "jobId")
  private List<NodeMaster> jobIds;

  @Column(name = "is_deleted")
  private Boolean isDeleted;

  @Column(name = "deleted_by", nullable = false)
  private String deletedBy;
}
