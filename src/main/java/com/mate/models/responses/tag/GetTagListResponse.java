package com.mate.models.responses.tag;

import com.mate.models.responses.BaseApiResponse;
import com.mate.repositories.projections.TagList;
import java.util.stream.Stream;
import lombok.Data;

@Data
public class GetTagListResponse extends BaseApiResponse {
  Stream<TagList> data;
}
