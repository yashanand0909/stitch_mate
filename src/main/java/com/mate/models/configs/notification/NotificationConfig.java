package com.mate.models.configs.notification;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mate.models.enums.NotificationMedium;
import javax.validation.constraints.NotNull;
import lombok.Data;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "notificationMedium",
    visible = true,
    include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({@JsonSubTypes.Type(value = SlackConfigSchema.class, name = "SLACK")})
@Data
public class NotificationConfig {

  @NotNull private NotificationMedium notificationMedium;
}
