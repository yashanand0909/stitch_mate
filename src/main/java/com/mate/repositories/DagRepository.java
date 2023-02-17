package com.mate.repositories;

import com.mate.models.entities.Dags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DagRepository extends JpaRepository<Dags, String> {

  @Query(value = "SELECT * FROM airflow_uat.dag where dag_id = :dagId", nativeQuery = true)
  Dags getByDagIdUat(String dagId);

  @Query(value = "SELECT * FROM airflow.dag where dag_id = :dagId ", nativeQuery = true)
  Dags getByDagId(String dagId);
}
