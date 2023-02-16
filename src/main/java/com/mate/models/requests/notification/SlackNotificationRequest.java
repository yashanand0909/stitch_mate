package com.mate.models.requests.notification;

import com.dream11.models.slack.Attachment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlackNotificationRequest {

  private String channelName;
  private Attachment attachment;
}
