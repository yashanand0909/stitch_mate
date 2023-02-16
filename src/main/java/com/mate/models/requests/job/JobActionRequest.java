package com.mate.models.requests.job;

import com.mate.models.enums.JobActions;
import com.mate.models.requests.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobActionRequest extends BaseRequest {

  private String jobName;
  private JobActions jobActions;
}
