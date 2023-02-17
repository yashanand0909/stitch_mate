package com.mate.repositories;

import com.mate.models.entities.NodeMaster;
import com.mate.repositories.projections.DestinationTable;
import com.mate.repositories.projections.JobNodeList;
import com.mate.repositories.projections.NodeList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NodeMasterRepository extends JpaRepository<NodeMaster, Long> {

  NodeMaster findByNodeId(long nodeId);

  NodeMaster findByNodeName(String nodeName);

  NodeMaster findByCreatedBy(String created_by);

  @Query("select nodeMaster from NodeMaster nodeMaster " + "where  nodeMaster.nodeId in (:nodeIds)")
  List<NodeMaster> findByNodeIdList(List<Long> nodeIds);

  NodeMaster findByNodeNameAndJobId(String nodeName, Long jobId);

  @Query(
      "select nodeMaster.jobId as jobId, nodeMaster.nodeId as nodeId from NodeMaster nodeMaster  where nodeMaster.jobId = :jobId "
          + "and nodeMaster.isDeleted = false")
  List<JobNodeList> findAllNodeOfJob(long jobId);

  @Query(
      "select n.nodeId as nodeId, n.nodeName as nodeName, n.metadata as metadata, n.isDeleted as isDeleted  from "
          + "NodeMaster  n where n"
          + ".jobId = :jobId and n"
          + ".isDeleted = false ")
  List<NodeList> getNodeListByJobId(Long jobId);

  @Query(
      "select n.destinationTable as destinationTable from NodeMaster n where n.destinationTable = :destinationTable "
          + "and n.isDeleted = false")
  DestinationTable getDestinationTableName(String destinationTable);

  @Query(
      "select n.destinationTable as destinationTable from NodeMaster n where n.destinationTable = :destinationTable and n.nodeId <> :nodeId")
  DestinationTable getNotDestinationTableNodeMaster(String destinationTable, Long nodeId);

  @Query(
      "select n from NodeMaster n where n.nodeName = :nodeName and n.jobId = :jobId and n.nodeId <> :nodeId")
  NodeMaster getNotNodeNameNodeMaster(String nodeName, Long jobId, Long nodeId);

  List<NodeMaster> findByJobId(Long jobId);
}
