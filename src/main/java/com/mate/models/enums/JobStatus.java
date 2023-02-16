package com.mate.models.enums;

public enum JobStatus {
  DRAFT("DRAFT"),
  ACTIVE("ACTIVE"),
  RUNNING("RUNNING"),
  PAUSED("PAUSED"),
  FAILED("FAILED"),
  EXPIRED("EXPIRED");

  private final String status;

  JobStatus(String status) {
    this.status = status;
  }
}
