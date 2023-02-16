package com.mate.models.requests.job;

import com.mate.models.configs.notification.NotificationConfig;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubmitJobRequest {
  boolean backfill = false;
  LocalDateTime endDate;
  @NotNull Long jobId;
  @NotNull String schedule;
  @NotNull NotificationConfig notificationConfig;
  LocalDateTime startDate = LocalDateTime.now();
}
