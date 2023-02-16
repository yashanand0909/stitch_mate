package com.mate.models.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class Message implements Serializable {
  private static final long serialVersionUID = 1985660773375149055L;
  private String channel;
  private String text;
  private String username;
  private List<Attachment> attachments;

  @JsonProperty("icon_emoji")
  private String iconEmoji;
  /**
   * Slack message pojo.
   *
   * @param messageBuilder message builder object
   */
  public Message(MessageBuilder messageBuilder) {
    this.channel = messageBuilder.getChannel();
    this.text = messageBuilder.getText();
    this.username = messageBuilder.getUsername();
    this.iconEmoji = messageBuilder.getIconEmoji();
    this.attachments = messageBuilder.getAttachments();
  }
}
