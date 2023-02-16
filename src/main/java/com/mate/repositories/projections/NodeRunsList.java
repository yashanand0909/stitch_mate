package com.mate.repositories.projections;

import com.dream11.models.enums.NodeStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Map;

public interface NodeRunsList {

  Long getNodeRunId();

  Long getNodeId();

  String getNodeName();

  NodeStatus getNodeStatus();

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime getCreatedAt();

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  LocalDateTime getRecupdatedAt();

  Map<String, Object> getMetadata();
}
