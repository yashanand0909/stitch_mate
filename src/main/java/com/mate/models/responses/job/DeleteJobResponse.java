package com.mate.models.responses.job;

import com.mate.models.responses.BaseApiResponse;
import lombok.Data;

@Data
public class DeleteJobResponse extends BaseApiResponse {

  Long jobId;
  Boolean jobDeleteStatus;
}
