package com.mate.models.responses.job;

import com.mate.models.responses.BaseApiResponse;
import lombok.Data;

@Data
public class GetJobRunDetailsResponse extends BaseApiResponse {

  private JobRunDetails data;
}
