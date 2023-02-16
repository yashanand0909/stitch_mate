package com.mate.models.responses.config;

import com.dream11.models.responses.BaseApiResponse;
import java.util.List;
import lombok.Data;

@Data
public class GetConfigValuesResponse extends BaseApiResponse {
  List<String> valuesList;
}
