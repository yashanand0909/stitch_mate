package com.mate.models.requests.tag;

import java.util.ArrayList;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddRemoveTagRequest {
  @NotNull private Long jobId;
  @NotNull private ArrayList<Long> tagId;
  private String user = "admin";
}
