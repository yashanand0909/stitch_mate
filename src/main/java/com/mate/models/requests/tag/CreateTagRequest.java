package com.mate.models.requests.tag;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateTagRequest {
  @Pattern(
      regexp = "^[a-zA-Z\\d\\-_]+$",
      message = "Only AlphaNumeric characters & hyphen(-) allowed")
  @NotNull
  private String tag;
}
