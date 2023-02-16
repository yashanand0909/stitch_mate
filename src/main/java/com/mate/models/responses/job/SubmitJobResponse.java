package com.mate.models.responses.job;

import com.mate.models.entities.JobMaster;
import com.mate.models.responses.BaseApiResponse;
import lombok.Data;

@Data
public class SubmitJobResponse extends BaseApiResponse {
  public JobMaster data;
}
