package com.mate.utilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mate.models.slack.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SlackUtils {

  private static final String SLACK_EMOJI = ":stitch2:";
  private static final String SLACK_USER = "DataStitch";

  public static Message buildMessage(String channel, Attachment attachment, String text) {
    MessageBuilder messageBuilder =
        new MessageBuilder()
            .setChannel(channel)
            .setUsername(SLACK_USER)
            .setIconEmoji(SLACK_EMOJI)
            .setText(text)
            .addAttachment(attachment);
    log.info("building message for slack");
    return messageBuilder.build();
  }

  public static String sendSlackMessage(
      String channel, Attachment attachment, String slack_Webhook_Url, String text)
      throws JsonProcessingException {
    ObjectMapper om = new ObjectMapper();
    om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    Message slackMessage = buildMessage(channel, attachment, text);
    String jsonPayload = om.writeValueAsString(slackMessage);
    log.info("sending slack message");
    return HttpUtils.postRequest(jsonPayload, slack_Webhook_Url);
  }
}
