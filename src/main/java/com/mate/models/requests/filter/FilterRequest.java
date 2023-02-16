package com.mate.models.requests.filter;

import com.dream11.models.enums.JobStatus;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class FilterRequest implements Serializable {

  private String createdBy;
  private String tag;
  private JobStatus status;
}
