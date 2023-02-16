package com.mate.repositories.projections;

import java.util.Map;

public interface NodeList {

  Long getNodeId();

  String getNodeName();

  Map<String, Object> getMetadata();

  Boolean getIsDeleted();
}
