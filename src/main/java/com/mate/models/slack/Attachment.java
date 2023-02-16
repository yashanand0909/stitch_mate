package com.mate.models.slack;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Attachment implements Serializable {

  public static final String COLOR_GOOD = "45DD5B";
  public static final String COLOR_INFO = "1192D7";
  public static final String COLOR_WARNING = "FDB243";
  public static final String COLOR_DANGER = "E34563";
  private String color;
  private List<Block> blocks;

  public Attachment(AttachmentBuilder attachmentBuilder) {
    this.setBlocks(attachmentBuilder.getBlocks());
    this.setColor(attachmentBuilder.getColor());
  }
}
