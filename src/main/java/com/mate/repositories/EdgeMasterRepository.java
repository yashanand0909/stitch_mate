package com.mate.repositories;

import com.mate.models.entities.EdgeMaster;
import com.mate.repositories.projections.EdgeList;
import com.mate.repositories.projections.JobChildNodeList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EdgeMasterRepository extends JpaRepository<EdgeMaster, Long> {

  EdgeMaster findByEdgeId(long edgeId);

  @Query(
      "select edgeMaster from EdgeMaster edgeMaster "
          + "where edgeMaster.jobId = :jobId and edgeMaster.fromNode = :fromNode and edgeMaster.toNode = :toNode and edgeMaster.isDeleted = "
          + "false")
  List<EdgeMaster> findByJobIdAndFromNodeAndToNode(Long jobId, Long fromNode, Long toNode);

  @Query(
      "select edgeMaster from EdgeMaster edgeMaster "
          + "where edgeMaster.fromNode = :nodeId or edgeMaster.toNode = :nodeId")
  List<EdgeMaster> findByNodeId(Long nodeId);

  @Query(
      "select edgeMaster.jobId as jobId, edgeMaster.toNode as toNode from EdgeMaster edgeMaster where  edgeMaster.jobId = :jobId  ")
  List<JobChildNodeList> findAllChildNodesOfJob(Long jobId);

  @Query(
      "select e.edgeId as edgeId, e.toNode as toNode, e.fromNode as fromNode from EdgeMaster e where e.jobId = :jobId and e.isDeleted = "
          + "false")
  List<EdgeList> getEdgeListByJobId(Long jobId);

  List<EdgeMaster> findByJobId(Long jobId);
}
