package com.mate.models.responses.notification;

import com.mate.models.responses.BaseApiResponse;
import lombok.Data;

@Data
public class NotificationResponse extends BaseApiResponse {

  Boolean isNotificationSent;
}
