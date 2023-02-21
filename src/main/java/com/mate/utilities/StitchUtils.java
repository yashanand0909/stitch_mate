package com.mate.utilities;

import com.mate.models.enums.NodeStatus;
import com.mate.models.responses.BaseApiResponse;
import com.mate.models.responses.airflow.DagRun;
import com.mate.models.responses.validation.ValidationResponse;
import com.mate.repositories.projections.NodeList;
import com.mate.repositories.projections.NodeRunsList;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StitchUtils {

  public static ValidationResponse updateValidationResponseOnFailure(
      ValidationResponse validationResponse, String comment) {
    validationResponse.setIsValid(false);
    if (validationResponse.getComment() != null)
      validationResponse.setComment(validationResponse.getComment() + " || " + comment);
    else validationResponse.setComment(comment);
    log.info("setting up validation response on failure");
    return validationResponse;
  }

  public static <T extends BaseApiResponse> T updateExceptionResponse(
      T responseBody, String message) {
    responseBody.setStatus("FAILED");
    responseBody.setMessage(message);
    return responseBody;
  }

  public static List<NodeRunsList> getRecentRunDetails(
      List<NodeRunsList> nodeRunsLists, List<NodeList> nodeLists) {
    log.info("sorting nodes based on id");
    nodeRunsLists.sort((a, b) -> (int) (a.getNodeId() - b.getNodeId()));
    List<NodeRunsList> finalNodeRuns = new ArrayList<>();
    Set<Long> addNodeSet = new HashSet<>();
    int i = 0;
    while (i < nodeRunsLists.size()) {
      int latest = i;
      while (i < nodeRunsLists.size() - 1
          && nodeRunsLists.get(i).getNodeId().equals(nodeRunsLists.get(i + 1).getNodeId())) {
        i += 1;
        if (nodeRunsLists.get(latest).getNodeRunId() <= nodeRunsLists.get(i).getNodeRunId())
          latest = i;
      }
      finalNodeRuns.add(nodeRunsLists.get(latest));
      addNodeSet.add(nodeRunsLists.get(latest).getNodeId());
      i++;
    }
    for (NodeList nodeList : nodeLists) {
      if (!addNodeSet.contains(nodeList.getNodeId())) {
        log.info("setting dummy values for node runs which never happened");
        finalNodeRuns.add(
            new NodeRunsList() {
              @Override
              public Long getNodeRunId() {
                return null;
              }

              @Override
              public Long getNodeId() {
                return nodeList.getNodeId();
              }

              @Override
              public String getNodeName() {
                return nodeList.getNodeName();
              }

              @Override
              public Map<String, Object> getMetadata() {
                return nodeList.getMetadata();
              }

              @Override
              public NodeStatus getNodeStatus() {
                return null;
              }

              @Override
              public LocalDateTime getCreatedAt() {
                return null;
              }

              @Override
              public LocalDateTime getRecupdatedAt() {
                return null;
              }
            });
      }
    }
    log.info("fetching recent run details");
    return finalNodeRuns;
  }

  public static DagRun setStatusUpperCase(DagRun dagRun) {

    dagRun.setStatus(dagRun.getStatus().toUpperCase());
    log.info("setting dag status to uppercase");
    return dagRun;
  }
}
