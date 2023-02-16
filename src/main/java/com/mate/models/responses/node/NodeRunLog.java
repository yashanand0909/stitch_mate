package com.mate.models.responses.node;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NodeRunLog {
  private Integer attempt;
  private String log;
}
