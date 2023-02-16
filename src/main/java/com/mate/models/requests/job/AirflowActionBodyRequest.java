package com.mate.models.requests.job;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AirflowActionBodyRequest {

  private boolean is_paused;
}
