package com.mate.models.responses.tag;

import com.mate.models.entities.TagMaster;
import com.mate.models.responses.BaseApiResponse;
import lombok.Data;

@Data
public class CreateTagResponse extends BaseApiResponse {
  private TagMaster data;
}
