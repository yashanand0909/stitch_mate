package com.mate.models.responses.job;

import com.mate.models.entities.JobMaster;
import com.mate.models.responses.BaseApiResponse;
import lombok.Data;

@Data
public class CreateJobResponse extends BaseApiResponse {
  private JobMaster data;
}
