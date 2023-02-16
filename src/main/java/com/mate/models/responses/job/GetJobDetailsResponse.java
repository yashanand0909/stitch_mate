package com.mate.models.responses.job;

import com.mate.models.responses.BaseApiResponse;
import lombok.Data;

@Data
public class GetJobDetailsResponse extends BaseApiResponse {

  private JobDetails data;
}
