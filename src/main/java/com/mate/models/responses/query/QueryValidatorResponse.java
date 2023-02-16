package com.mate.models.responses.query;

import com.mate.models.responses.BaseApiResponse;
import java.util.List;
import lombok.Data;

@Data
public class QueryValidatorResponse extends BaseApiResponse {

  Boolean valid;
  List<String> parameters;
}
