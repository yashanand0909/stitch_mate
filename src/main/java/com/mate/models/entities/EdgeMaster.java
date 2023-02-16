package com.mate.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "edge_master")
public class EdgeMaster {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "edge_id")
  private long edgeId;

  @Column(name = "job_id")
  private long jobId;

  @Column(name = "to_node", nullable = false)
  private long toNode;

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted;

  @Column(name = "from_node", nullable = false)
  private long fromNode;

  @Column(name = "created_by", nullable = false)
  private String createdBy;

  @Column(name = "deleted_by")
  private String deletedBy;

  @CreationTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(name = "recupdated_at", nullable = false)
  private LocalDateTime recupdatedAt;
}
