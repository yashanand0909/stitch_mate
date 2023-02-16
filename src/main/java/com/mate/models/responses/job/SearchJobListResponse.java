package com.mate.models.responses.job;

import com.mate.models.requests.job.JobsListingPageDetail;
import com.mate.models.responses.BaseApiResponse;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchJobListResponse extends BaseApiResponse {

  public SearchJobListResponse(
      String status,
      String message,
      List<JobListResponse> data,
      JobsListingPageDetail jobsListingPageDetail,
      Long totalJobs) {
    super(status, message);
    this.data = data;
    this.jobsListingPageDetail = jobsListingPageDetail;
    this.totalJobs = totalJobs;
  }

  private List<JobListResponse> data;
  private JobsListingPageDetail jobsListingPageDetail;
  private Long totalJobs;
}
