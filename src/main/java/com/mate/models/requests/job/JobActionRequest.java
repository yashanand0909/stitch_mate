package com.mate.models.requests.job;

import com.dream11.models.enums.JobActions;
import com.dream11.models.requests.BaseRequest;
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
