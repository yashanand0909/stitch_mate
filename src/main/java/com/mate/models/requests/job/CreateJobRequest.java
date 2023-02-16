package com.mate.models.requests.job;

import java.util.ArrayList;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateJobRequest {

  @Pattern(regexp = "^[a-zA-Z\\d\\-_]+$", message = "Only AlphaNumeric characters & - , _ allowed")
  private String jobName;

  private ArrayList<Long> tags;
  private String description;
}
