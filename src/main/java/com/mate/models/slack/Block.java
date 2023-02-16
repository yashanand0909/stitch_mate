package com.mate.models.slack;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Block {
  private String type;
  private BlockFields text;
  private List<BlockFields> fields;

  /**
   * Block Constructor.
   *
   * @param blockFields blockFields to be added to Block
   * @return returns Block
   */
  public Block addField(BlockFields blockFields) {
    if (fields == null) {
      fields = new ArrayList<>();
    }
    fields.add(blockFields);
    return this;
  }
}
