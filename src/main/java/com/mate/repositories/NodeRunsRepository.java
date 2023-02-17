package com.mate.repositories;

import com.mate.models.entities.NodeRuns;
import com.mate.repositories.projections.NodeRunsList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NodeRunsRepository extends JpaRepository<NodeRuns, Long> {

  NodeRuns findByNodeName(String nodeName);

  NodeRuns findByUpdatedBy(String update_by);

  @Query(
      "select n.nodeRunId as nodeRunId, n.nodeId as nodeId, n.nodeName as nodeName, n.nodeStatus as nodeStatus, n"
          + ".createdAt as createdAt, n.recupdatedAt as recupdatedAt, n.metadata as metadata  from NodeRuns n where"
          + " n.dagRunId = "
          + ":dagRunId and n.jobId = :jobId")
  List<NodeRunsList> getNodeRunsListByDagRunId(String dagRunId, Long jobId);

  @Query(
      "select n.nodeRunId as nodeRunId, n.nodeId as nodeId, n.nodeName as nodeName, n.nodeStatus as nodeStatus, n.createdAt as createdAt, n.recupdatedAt as recupdatedAt  from NodeRuns n where n.dagRunId = :dagRunId and nodeId = :nodeId order by n.createdAt")
  List<NodeRunsList> getNodeRunsByDagRunIdAndNodeId(String dagRunId, Long nodeId);
}
