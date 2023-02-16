package com.mate.models.enums;

public enum NodeStatus {
  RUNNING("RUNNING"),
  SUCCESS("SUCCESS"),
  FAILED("FAILED");

  private final String status;

  NodeStatus(String status) {
    this.status = status;
  }
}
