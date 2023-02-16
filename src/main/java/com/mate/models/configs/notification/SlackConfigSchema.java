package com.mate.models.configs.notification;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SlackConfigSchema extends NotificationConfig {

  @NotNull private String channelName;
}
