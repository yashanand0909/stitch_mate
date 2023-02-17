package com.mate.repositories;

import com.mate.models.entities.DagRuns;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DagRunsRepository extends JpaRepository<DagRuns, Long> {

  @Query(
      value =
          "SELECT * FROM airflow_uat.dag_run where dag_id = :dagId order by execution_Date desc limit 10 ",
      nativeQuery = true)
  List<DagRuns> getByDagIdUat(String dagId);

  @Query(
      value =
          "SELECT * FROM airflow.dag_run where dag_id = :dagId order by execution_Date desc limit 10 ",
      nativeQuery = true)
  List<DagRuns> getByDagId(String dagId);
}
