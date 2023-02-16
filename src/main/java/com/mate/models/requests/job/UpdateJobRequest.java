package com.mate.models.requests.job;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateJobRequest {
  @NotNull private long jobId;
  private LocalDateTime endDate;
  private String description;
}
