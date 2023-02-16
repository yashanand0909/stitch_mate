package com.mate.models.slack;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttachmentBuilder {

  private List<Block> blocks;
  private String color;

  public Attachment build() {
    return new Attachment(this);
  }

  /**
   * Added Attachment.
   *
   * @param block block to be attached to attachment
   * @return return AttachmentBuilder
   */
  public AttachmentBuilder addField(Block block) {
    if (blocks == null) {
      blocks = new ArrayList<>();
    }
    blocks.add(block);
    return this;
  }
}
