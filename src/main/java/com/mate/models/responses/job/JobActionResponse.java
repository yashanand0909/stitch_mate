package com.mate.models.responses.job;

import com.mate.models.entities.JobMaster;
import com.mate.models.responses.BaseApiResponse;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobActionResponse extends BaseApiResponse {

  private JobMaster jobMaster;
}
