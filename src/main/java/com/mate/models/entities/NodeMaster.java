package com.mate.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mate.models.converter.HashMapConverter;
import com.mate.models.enums.Source;
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

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "node_master")
public class NodeMaster {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "node_id")
  private long nodeId;

  @Column(name = "node_name", nullable = false)
  private String nodeName;

  @Column(name = "job_id", nullable = false)
  private long jobId;

  @Column(name = "source", nullable = false)
  @Enumerated(EnumType.STRING)
  private Source source;

  @Column(name = "created_by", nullable = false)
  private String createdBy;

  @Column(name = "updated_by")
  private String updatedBy;

  @Column(name = "query", nullable = false)
  private String query;

  @Column(name = "is_edited", nullable = false)
  private Boolean isEdited;

  @Column(name = "destination_config")
  private String destinationConfig;

  @Column(name = "parameter_config")
  private String parameterConfig;

  @Column(name = "is_deleted")
  private boolean isDeleted;

  @Column(name = "destination_table")
  private String destinationTable;

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
