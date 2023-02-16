package com.mate.models.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Data
@Entity
@Table(name = "tag_master")
@NoArgsConstructor
public class TagMaster implements Serializable {

  private static final long serialVersionUID = -3095129847409027457L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tag_id")
  private long tagId;

  @Column(name = "tag", nullable = false, unique = true)
  private String tag;

  @Column(name = "tag_description")
  private String tagDescription;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "created_by")
  private String createdBy;
}
