package com.mate.models.responses.config;

import com.dream11.models.entities.ConfigMaster;
import com.dream11.models.responses.BaseApiResponse;
import lombok.Data;

@Data
public class ConfigResponse extends BaseApiResponse {
  ConfigMaster config;
}
