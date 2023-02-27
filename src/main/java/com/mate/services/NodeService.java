package com.mate.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mate.constants.Constants;
import com.mate.exceptions.StitchMateGenericException;
import com.mate.models.configs.destination.RedshiftConnectionConfig;
import com.mate.models.entities.EdgeMaster;
import com.mate.models.entities.NodeMaster;
import com.mate.models.requests.edge.DeleteEdgeRequest;
import com.mate.models.requests.node.CreateNodeRequest;
import com.mate.models.requests.node.DeleteNodeRequest;
import com.mate.models.requests.node.EditNodeRequest;
import com.mate.models.requests.query.QueryValidatorRequest;
import com.mate.models.responses.node.NodeRunLog;
import com.mate.models.responses.query.QueryValidatorResponse;
import com.mate.models.responses.validation.ValidationResponse;
import com.mate.repositories.EdgeMasterRepository;
import com.mate.repositories.NodeMasterRepository;
import com.mate.repositories.NodeRunsRepository;
import com.mate.repositories.projections.NodeRunsList;
import com.mate.utilities.HttpUtils;
import com.mate.utilities.ParserUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NodeService {

  @Value("${airflow.api.url}")
  private String airflowApiUrl;

  @Value("${airflow.api.auth}")
  private String authorization;

  @Value("${airflow.api.session}")
  private String session;

  NodeMasterRepository nodeMasterRepository;
  EdgeMasterRepository edgeMasterRepository;
  NodeRunsRepository nodeRunsRepository;
  EdgeService edgeService;

  public NodeService(
      NodeMasterRepository nodeMasterRepository,
      EdgeMasterRepository edgeMasterRepository,
      EdgeService edgeService,
      NodeRunsRepository nodeRunsRepository) {
    this.nodeMasterRepository = nodeMasterRepository;
    this.edgeMasterRepository = edgeMasterRepository;
    this.edgeService = edgeService;
    this.nodeRunsRepository = nodeRunsRepository;
  }

  public NodeMaster createNode(CreateNodeRequest createNodeRequest, String createdBy)
      throws JsonProcessingException {
    RedshiftConnectionConfig connectionConfig = null;
    ObjectMapper objectMapper = new ObjectMapper();
    NodeMaster nodeMaster = new NodeMaster();
    nodeMaster.setNodeName(createNodeRequest.getNodeName());
    nodeMaster.setJobId(createNodeRequest.getJobId());
    nodeMaster.setSource(createNodeRequest.getSource());
    nodeMaster.setCreatedBy(createdBy);
    nodeMaster.setUpdatedBy(createdBy);
    nodeMaster.setQuery(createNodeRequest.getQuery());
    createNodeRequest
        .getMetadata()
        .put(
            "query_comment",
            ParserUtils.addUserDetailsInMetadata(createdBy, createNodeRequest.getJobName()));
    nodeMaster.setMetadata(createNodeRequest.getMetadata());
    nodeMaster.setDestinationConfig(
        objectMapper.writeValueAsString(createNodeRequest.getDestinationConfig()));
    switch (createNodeRequest.getDestinationConfig().getConnectorType()) {
      case REDSHIFT:
        connectionConfig = (RedshiftConnectionConfig) createNodeRequest.getDestinationConfig();
        nodeMaster.setDestinationTable(
            connectionConfig.getSchema() + "." + connectionConfig.getTableName().toLowerCase());
        log.info("selected redshift as destination");
        break;
      default:
        break;
    }
    if (createNodeRequest.getParameterConfig() != null) {
      nodeMaster.setParameterConfig(
          objectMapper.writeValueAsString(createNodeRequest.getParameterConfig()));
      log.info("setting paramerters");
    } else {
      nodeMaster.setParameterConfig("{\"parametersList\":[]}");
      log.info("no paramerters passed");
    }
    try {
      nodeMaster = nodeMasterRepository.save(nodeMaster);
      log.info("created node: {}", nodeMaster);
      return nodeMaster;
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new StitchMateGenericException(
          "Internal error occurred while creating the node with message - " + e.getMessage());
    }
  }

  public NodeMaster deleteNode(DeleteNodeRequest deleteNodeRequest, String deletedBy) {
    try {
      boolean deleteAll = false;
      int count = 1;
      while (!deleteAll && count <= 3) {
        deleteAll = deleteAllNodeEdges(deleteNodeRequest, deletedBy);
        count++;
        log.info("deleting nodes");
      }
      if (!deleteAll)
        throw new StitchMateGenericException(
            "Unable to delete all attached edges for node Id - " + deleteNodeRequest.getNodeId());
      log.error("failed to delete nodes");
      NodeMaster nodeMaster = nodeMasterRepository.findByNodeId(deleteNodeRequest.getNodeId());
      nodeMaster.setDeleted(true);
      nodeMaster.setUpdatedBy(deletedBy);
      nodeMaster = nodeMasterRepository.save(nodeMaster);
      log.info("deleted nodes {}", deleteNodeRequest.getNodeId());
      return nodeMaster;
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new StitchMateGenericException(
          "Failed to delete node with nodeId - " + deleteNodeRequest.getNodeId());
    }
  }

  private boolean deleteAllNodeEdges(DeleteNodeRequest deleteNodeRequest, String deletedBy) {
    List<EdgeMaster> edgeList =
        edgeMasterRepository.findByNodeId(deleteNodeRequest.getNodeId()).stream()
            .filter(a -> !a.isDeleted())
            .map(
                a -> {
                  try {
                    log.info("deleting edges");
                    return edgeService.deleteEdge(new DeleteEdgeRequest(a.getEdgeId()), deletedBy);
                  } catch (Exception e) {
                    log.error("failed to delete edges: {}", e.getMessage());
                    return a;
                  }
                })
            .collect(Collectors.toList());

    for (EdgeMaster edgeMaster : edgeList) {
      if (edgeMaster == null || !edgeMaster.isDeleted()) return false;
    }
    return true;
  }

  public NodeMaster editNode(EditNodeRequest editNodeRequest, String editedBy)
      throws JsonProcessingException {

    ObjectMapper objectMapper = new ObjectMapper();

    NodeMaster nodeMaster = nodeMasterRepository.findByNodeId(editNodeRequest.getNodeId());
    if (Objects.nonNull(editNodeRequest.getNodeName()))
      nodeMaster.setNodeName(editNodeRequest.getNodeName());
    if (Objects.nonNull(editNodeRequest.getQuery()))
      nodeMaster.setQuery(editNodeRequest.getQuery());
    if (Objects.nonNull(editNodeRequest.getSource()))
      nodeMaster.setSource(editNodeRequest.getSource());
    nodeMaster.setUpdatedBy(editedBy);
    nodeMaster.setIsEdited(true);
    editNodeRequest
        .getMetadata()
        .put(
            "query_comment",
            ParserUtils.addUserDetailsInMetadata(editedBy, editNodeRequest.getJobName()));
    nodeMaster.setMetadata(editNodeRequest.getMetadata());
    if (Objects.nonNull(editNodeRequest.getDestinationConfig()))
      nodeMaster.setDestinationConfig(
          objectMapper.writeValueAsString(editNodeRequest.getDestinationConfig()));
    try {
      if (editNodeRequest.getParameterConfig() != null) {
        log.info("setting paramerters");
        nodeMaster.setParameterConfig(
            objectMapper.writeValueAsString(editNodeRequest.getParameterConfig()));
      }
      nodeMaster = nodeMasterRepository.save(nodeMaster);
      log.info("edited node master: {}", nodeMaster);
      return nodeMaster;
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new StitchMateGenericException(
          "Internal error occurred while updating the node with message - " + e.getMessage());
    }
  }

  public NodeMaster getNodeDetails(Long nodeId) {

    NodeMaster nodeMaster = nodeMasterRepository.findByNodeId(nodeId);
    if (nodeMaster != null) {
      log.info("fetched node details: {}", nodeMaster);
      return nodeMaster;
    } else {
      String comment = "failed to fetch node details";
      log.error(comment);
      throw new StitchMateGenericException(comment);
    }
  }

  public QueryValidatorResponse queryParser(QueryValidatorRequest queryValidatorRequest) {

    QueryValidatorResponse queryValidatorResponse = new QueryValidatorResponse();
    try {
      ValidationResponse validationResponse =
          ParserUtils.queryValidator(queryValidatorRequest.getQuery());
      if (validationResponse.getIsValid()) {
        List<String> parameters = ParserUtils.fetchColumns(queryValidatorRequest.getQuery());
        queryValidatorResponse.setParameters(parameters);
        log.info("setting parameters found in the query");
      }
      queryValidatorResponse.setValid(validationResponse.getIsValid());
      queryValidatorResponse.setMessage(validationResponse.getComment());
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new StitchMateGenericException(
          "Internal error occurred while updating the node with message - " + e.getMessage());
    }
    log.info("validated query response: {}", queryValidatorResponse);
    return queryValidatorResponse;
  }

  public List<NodeRunLog> getNodeRunLogs(String jobName, String dagRunId, Long nodeId) {
    try {
      List<NodeRunsList> nodeRunsLists =
          nodeRunsRepository.getNodeRunsByDagRunIdAndNodeId(dagRunId, nodeId);
      List<NodeRunLog> nodeRunLogList = new ArrayList<>();
      for (int i = 0; i < nodeRunsLists.size(); i++) {
        String log =
            HttpUtils.get(
                String.format(
                    "%s%s%s/%s%s/%s%s/%s%s",
                    airflowApiUrl,
                    Constants.dagEndpoint,
                    jobName,
                    Constants.dagRunsEndpoint,
                    dagRunId,
                    Constants.taskInstancesEndPoint,
                    nodeId,
                    Constants.logEndpoint,
                    i + 1),
                authorization,
                session);
        NodeRunLog nodeRunLog = new NodeRunLog(i + 1, log);
        nodeRunLogList.add(nodeRunLog);
      }
      log.info("node runs logs: {}", nodeRunLogList);
      return nodeRunLogList;
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new StitchMateGenericException(
          "Internal Exception occurred while getting logs with error - " + e.getMessage());
    }
  }
}
