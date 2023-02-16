package com.mate.models.responses.config;

import com.mate.models.entities.ConfigMaster;
import com.mate.models.responses.BaseApiResponse;
import lombok.Data;

@Data
public class ConfigResponse extends BaseApiResponse {
  ConfigMaster config;
}
