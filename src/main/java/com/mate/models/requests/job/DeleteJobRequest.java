package com.mate.models.requests.job;

import com.dream11.models.requests.BaseRequest;
import lombok.Data;

@Data
public class DeleteJobRequest extends BaseRequest {

  private Boolean isDelete;
}
