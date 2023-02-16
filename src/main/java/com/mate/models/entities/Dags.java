package com.mate.models.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "dag", catalog = "airflow")
public class Dags {

  @Id
  @Column(name = "dag_id")
  private String dagId;

  @Column(name = "next_dagrun")
  private String nextDagRun;
}
