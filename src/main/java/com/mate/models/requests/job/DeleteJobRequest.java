package com.mate.models.requests.job;

import com.mate.models.requests.BaseRequest;
import lombok.Data;

@Data
public class DeleteJobRequest extends BaseRequest {

  private Boolean isDelete;
}
