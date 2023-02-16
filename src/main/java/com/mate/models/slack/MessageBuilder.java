package com.mate.models.slack;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageBuilder {
  private List<Attachment> attachments;
  private String channel;
  private String iconEmoji;
  private String iconUrl;
  private String text;
  private String username;

  public Message build() {
    return new Message(this);
  }

  public String getChannel() {
    return channel;
  }

  public MessageBuilder setChannel(String channel) {
    this.channel = channel;
    return this;
  }

  public String getText() {
    return text;
  }

  public MessageBuilder setText(String text) {
    this.text = text;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public MessageBuilder setUsername(String username) {
    this.username = username;
    return this;
  }

  public MessageBuilder setIconEmoji(String iconEmoji) {
    this.iconEmoji = iconEmoji;
    return this;
  }

  public List<Attachment> getAttachments() {
    return attachments;
  }

  /**
   * Add attachment to Slack Message.
   *
   * @param attachment attachment to be added to Sack Message
   * @return returns Slack Message
   */
  public MessageBuilder addAttachment(Attachment attachment) {
    if (attachments == null) {
      attachments = new ArrayList<>();
    }
    attachments.add(attachment);
    return this;
  }
}
