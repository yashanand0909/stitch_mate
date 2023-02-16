package com.mate.models.requests.job;

import com.dream11.models.requests.filter.FilterRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class SearchJobRequest {

  private String jobName;
  private String tableName;
  FilterRequest filterRequest;
  JobsListingPageDetail jobsListingPageDetail;
}
