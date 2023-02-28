package com.mate.controllers.v1;

import static com.mate.constants.Constants.msdUserEmailHeader;

import com.mate.exceptions.InvalidRequestException;
import com.mate.models.entities.JobMaster;
import com.mate.models.requests.job.*;
import com.mate.models.requests.job.CreateJobRequest;
import com.mate.models.requests.job.JobActionRequest;
import com.mate.models.requests.job.SubmitJobRequest;
import com.mate.models.requests.tag.AddRemoveTagRequest;
import com.mate.models.responses.BaseApiResponse;
import com.mate.models.responses.airflow.DagRun;
import com.mate.models.responses.job.*;
import com.mate.models.responses.validation.ValidationResponse;
import com.mate.services.JobService;
import com.mate.services.ValidationService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/v1/job")
public class JobController {

  final JobService jobService;
  final ValidationService validationService;

  public JobController(JobService jobService, ValidationService validationService) {
    this.jobService = jobService;
    this.validationService = validationService;
  }

  @PostMapping(produces = "application/json")
  public ResponseEntity<CreateJobResponse> create(
      @Valid @RequestBody CreateJobRequest createJobRequest,
      @NotNull @NotBlank @NotEmpty @RequestHeader(msdUserEmailHeader) String createdBy) {

    JobMaster jobMaster = jobService.createJob(createJobRequest, createdBy);
    CreateJobResponse createJobResponse = new CreateJobResponse();
    createJobResponse.setData(jobMaster);
    createJobResponse.setMessage("Job Created");
    return new ResponseEntity<>(createJobResponse, HttpStatus.OK);
  }

  @PostMapping(value = "/submit", produces = "application/json")
  public ResponseEntity<SubmitJobResponse> submitJob(
      @Valid @RequestBody SubmitJobRequest submitJobRequest,
      @NotNull @NotBlank @NotEmpty @RequestHeader(msdUserEmailHeader) String actionBy) {
    SubmitJobResponse submitJobResponse = new SubmitJobResponse();
    submitJobResponse.setMessage("Job Scheduled");
    JobMaster jobMaster = jobService.activateJob(submitJobRequest, actionBy);
    submitJobResponse.setData(jobMaster);
    return new ResponseEntity<>(submitJobResponse, HttpStatus.OK);
  }

  @GetMapping(value = "/{jobId}/details", produces = "application/json")
  public ResponseEntity<GetJobDetailsResponse> getJobDetails(
      @PathVariable(value = "jobId") @NotNull Long jobId) {
    GetJobDetailsResponse getJobDetailsResponse = new GetJobDetailsResponse();
    JobDetails jobDetails = jobService.getJobDetails(jobId);
    getJobDetailsResponse.setData(jobDetails);
    getJobDetailsResponse.setMessage(String.format("Job Details for Job %s", jobId));
    return new ResponseEntity<>(getJobDetailsResponse, HttpStatus.OK);
  }

  @GetMapping(value = "/{jobId}/run/details", produces = "application/json")
  public ResponseEntity<GetJobRunDetailsResponse> getJobRunDetails(
      @PathVariable(value = "jobId") @NotNull Long jobId,
      @RequestParam(name = "jobRunId") @NotNull String jobRunId) {
    GetJobRunDetailsResponse getJobRunDetailsResponse = new GetJobRunDetailsResponse();
    JobRunDetails jobDetails = jobService.getJobRunDetails(jobId, jobRunId);
    getJobRunDetailsResponse.setData(jobDetails);
    getJobRunDetailsResponse.setMessage(String.format("Job Details for Job %s", jobId));
    return new ResponseEntity<>(getJobRunDetailsResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "/actions", method = RequestMethod.PATCH, consumes = "application/json")
  public ResponseEntity<JobActionResponse> jobActions(
      @Valid @RequestBody JobActionRequest jobActionRequest,
      @NotNull @NotBlank @NotEmpty @RequestHeader(msdUserEmailHeader) String actionBy) {
    ValidationResponse validationResponse =
        validationService.checkIfJobExistsByIdAndName(jobActionRequest);
    if (!validationResponse.getIsValid())
      throw new InvalidRequestException(validationResponse.getComment());
    JobActionResponse jobActionResponse = new JobActionResponse();
    JobMaster jobMaster = jobService.jobPauseResume(jobActionRequest, actionBy);
    jobActionResponse.setJobMaster(jobMaster);
    if (jobMaster != null) {
      jobActionResponse.setMessage(
          String.format(
              "Job: %s  %s ",
              jobActionRequest.getJobName(), jobActionRequest.getJobActions().name()));
      return new ResponseEntity<>(jobActionResponse, HttpStatus.OK);
    } else {
      jobActionResponse.setStatus("FAILED");
      jobActionResponse.setMessage(
          String.format(
              "Job: %s  %s Failed",
              jobActionRequest.getJobName(), jobActionRequest.getJobActions().name()));
      jobActionResponse.setJobMaster(jobMaster);
      return new ResponseEntity<>(jobActionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping(value = "/{jobName}/jobRuns", produces = "application/json")
  public ResponseEntity<JobRunsResponse> getJobRuns(
      @PathVariable(value = "jobName") @NotNull String jobName) {
    if (!validationService.checkIfJobNameExists(jobName))
      throw new InvalidRequestException("JobName Does not exists. Please enter a valid job Name");
    JobRunsResponse jobRunsResponse = new JobRunsResponse();
    List<DagRun> jobRuns = jobService.getJobRuns(jobName);
    jobRunsResponse.setJobRuns(jobRuns);
    jobRunsResponse.setMessage(String.format("Job runs for Job %s", jobName));
    return new ResponseEntity<>(jobRunsResponse, HttpStatus.OK);
  }

  @PostMapping(value = "/search", produces = "application/json")
  public ResponseEntity<SearchJobListResponse> Searchlist(
      @RequestBody SearchJobRequest searchJobRequest) {
    SearchJobListResponse searchJobListResponse = jobService.getSearch(searchJobRequest);
    return new ResponseEntity<>(searchJobListResponse, HttpStatus.OK);
  }

  @PutMapping(value = "/add-tag", consumes = "application/json")
  public ResponseEntity<BaseApiResponse> addTag(
      @Valid @RequestBody AddRemoveTagRequest addRemoveTagRequest) {
    BaseApiResponse baseApiResponse = new BaseApiResponse();
    jobService.addTagToJob(addRemoveTagRequest);
    baseApiResponse.setMessage("Tag added");
    return new ResponseEntity<>(baseApiResponse, HttpStatus.OK);
  }

  @DeleteMapping(value = "/remove-tag", consumes = "application/json")
  public ResponseEntity<BaseApiResponse> removeTag(
      @Valid @RequestBody AddRemoveTagRequest addRemoveTagRequest) {
    BaseApiResponse baseApiResponse = new BaseApiResponse();
    jobService.removeTagFromJob(addRemoveTagRequest);
    baseApiResponse.setMessage("Tag removed");
    return new ResponseEntity<>(baseApiResponse, HttpStatus.OK);
  }

  @PatchMapping(consumes = "application/json")
  public ResponseEntity<UpdateJobResponse> updateJob(
      @RequestBody UpdateJobRequest updateJobRequest) {
    UpdateJobResponse updateJobResponse = new UpdateJobResponse();
    JobMaster updatedJobDetails = jobService.updateJob(updateJobRequest);
    updateJobResponse.setMessage("Job Updated");
    updateJobResponse.setData(updatedJobDetails);
    return new ResponseEntity<>(updateJobResponse, HttpStatus.OK);
  }

  @DeleteMapping(consumes = "application/json")
  public ResponseEntity<DeleteJobResponse> deleteJob(
      @RequestBody DeleteJobRequest deleteJobRequest,
      @NotNull @NotBlank @NotEmpty @RequestHeader(msdUserEmailHeader) String deletedBy) {
    DeleteJobResponse deleteJobResponse = new DeleteJobResponse();
    JobMaster deletedJobdetails = jobService.deleteJob(deleteJobRequest, deletedBy);
    deleteJobResponse.setMessage("Job Deleted");
    deleteJobResponse.setJobId(deletedJobdetails.getJobId());
    deleteJobResponse.setJobDeleteStatus(deletedJobdetails.getIsDeleted());
    return new ResponseEntity<>(deleteJobResponse, HttpStatus.OK);
  }
}
