package com.mate.models.requests.job;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@NoArgsConstructor
public class JobsListingPageDetail {
  private int pageNumber = 0;
  private int pageSize = 10;
  private String sortBy = "jobId";
  private Sort.Direction sortDirection = Sort.Direction.DESC;
}
