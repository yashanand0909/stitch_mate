package com.mate.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mate.constants.Constants;
import com.mate.exceptions.StitchMateBadRequestException;
import com.mate.exceptions.StitchMateGenericException;
import com.mate.models.entities.*;
import com.mate.models.enums.JobActions;
import com.mate.models.enums.JobStatus;
import com.mate.models.requests.job.*;
import com.mate.models.requests.tag.AddRemoveTagRequest;
import com.mate.models.responses.airflow.DagRun;
import com.mate.models.responses.airflow.DagRunsResponse;
import com.mate.models.responses.job.JobDetails;
import com.mate.models.responses.job.JobListResponse;
import com.mate.models.responses.job.JobRunDetails;
import com.mate.models.responses.job.SearchJobListResponse;
import com.mate.repositories.*;
import com.mate.repositories.custom.JobMasterCustomImpl;
import com.mate.repositories.projections.EdgeList;
import com.mate.repositories.projections.NodeList;
import com.mate.repositories.projections.NodeRunsList;
import com.mate.repositories.projections.TagList;
import com.mate.utilities.HttpUtils;
import com.mate.utilities.StitchUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class JobService {

  @Value("${airflow.api.url}")
  private String airflowApiUrl;

  @Value("${airflow.api.auth}")
  private String authorization;

  @Value("${airflow.api.session}")
  private String session;

  @Value("${spring.liquibase.contexts}")
  private String profile;

  final JobMasterRepository jobMasterRepository;

  final NodeMasterRepository nodeMasterRepository;

  final EdgeMasterRepository edgeMasterRepository;

  final ValidationService validationService;

  final JobMasterCustomImpl jobMasterCustom;

  final NodeRunsRepository nodeRunsRepository;

  final JobsTagsRelationsRepository jobsTagsRelationsRepository;

  final ObjectMapper objectMapper;

  final ConfigService configService;

  final TagMasterRepository tagMasterRepository;

  final NotificationService notificationService;

  final DagRunsRepository dagRunsRepository;

  final DagRepository dagsRepository;

  public JobService(
      JobMasterRepository jobMasterRepository,
      NodeMasterRepository nodeMasterRepository,
      EdgeMasterRepository edgeMasterRepository,
      ValidationService validationService,
      JobMasterCustomImpl jobMasterCustom,
      NodeRunsRepository nodeRunsRepository,
      JobsTagsRelationsRepository jobsTagsRelationsRepository,
      ObjectMapper objectMapper,
      ConfigService configService,
      TagMasterRepository tagMasterRepository,
      NotificationService notificationService,
      DagRunsRepository dagRunsRepository,
      DagRepository dagsRepository) {
    this.jobMasterRepository = jobMasterRepository;
    this.nodeMasterRepository = nodeMasterRepository;
    this.edgeMasterRepository = edgeMasterRepository;
    this.validationService = validationService;
    this.jobMasterCustom = jobMasterCustom;
    this.nodeRunsRepository = nodeRunsRepository;
    this.jobsTagsRelationsRepository = jobsTagsRelationsRepository;
    this.objectMapper = objectMapper;
    this.configService = configService;
    this.tagMasterRepository = tagMasterRepository;
    this.notificationService = notificationService;
    this.dagRunsRepository = dagRunsRepository;
    this.dagsRepository = dagsRepository;
  }

  public JobMaster createJob(CreateJobRequest createJobRequest, String createdBy) {
    JobMaster jobMaster = new JobMaster();
    BeanUtils.copyProperties(createJobRequest, jobMaster);
    jobMaster.setJobName(createJobRequest.getJobName());
    jobMaster.setCreatedBy(createdBy);
    if (!validationService.checkIfJobNameExists(createJobRequest.getJobName())) {
      jobMaster.setJobStatus(JobStatus.DRAFT);
      jobMaster.setIsDeleted(false);
      jobMaster = jobMasterRepository.save(jobMaster);
      configService.updateCreatedByConfig(jobMaster.getCreatedBy());
      log.info("create job: {}", jobMaster);
      return jobMaster;
    } else {
      log.error("create job Validation Failed");
      throw new StitchMateGenericException(
          String.format("Job with name: %s already exists", jobMaster.getJobName()));
    }
  }

  private Set<Long> getAllJobNodes(long jobId) {
    Set<Long> nodeList = new HashSet<>();
    nodeMasterRepository.findAllNodeOfJob(jobId).forEach(data -> nodeList.add(data.getNodeId()));
    if (nodeList.isEmpty()) {
      log.error("Job has 0 nodes");
      throw new StitchMateGenericException("Job Has 0 Nodes defined");
    }
    log.info("All the JobNodes {} for Jobid =  : {}", nodeList, jobId);
    return nodeList;
  }

  private Set<Long> getAllChildNodes(long jobId) {
    Set<Long> childNodes = new HashSet<>();
    try {
      edgeMasterRepository
          .findAllChildNodesOfJob(jobId)
          .forEach(data -> childNodes.add(data.getToNode()));
      log.info("Child nodes: {}", childNodes);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return childNodes;
  }

  private Set<Long> getStartNodes(Set<Long> nodeMasterList, Set<Long> jobChildNodeListList) {
    try {
      nodeMasterList.removeAll(jobChildNodeListList);
      log.info("start nodes: {}", nodeMasterList);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return nodeMasterList;
  }

  public JobMaster activateJob(SubmitJobRequest submitJobRequest, String actionBy) {
    if (validationService.checkIfJobIdExistsWithValidStatus(submitJobRequest.getJobId())) {
      try {
        Set<Long> nodeMasterList = getAllJobNodes(submitJobRequest.getJobId());
        if (nodeMasterList.isEmpty()) {
          throw new StitchMateGenericException("Job with 0 Nodes cannot be started");
        }
        Set<Long> jobChildNodeListList = getAllChildNodes(submitJobRequest.getJobId());
        Set<Long> startNodeList = getStartNodes(nodeMasterList, jobChildNodeListList);
        JobMaster job =
            jobMasterRepository.findById(submitJobRequest.getJobId()).isPresent()
                ? jobMasterRepository.findById(submitJobRequest.getJobId()).get()
                : null;
        assert job != null;
        job.setStartNode(
            startNodeList.toString().replace("[", "").replace("]", "").replace(" ", ""));
        job.setBackfill(submitJobRequest.isBackfill());
        if (submitJobRequest.getStartDate() != null)
          job.setStartDate(submitJobRequest.getStartDate());
        if (submitJobRequest.getEndDate() != null) job.setEndDate(submitJobRequest.getEndDate());
        job.setSchedule(submitJobRequest.getSchedule());
        if (Objects.nonNull(submitJobRequest.getNotificationConfig()))
          job.setNotificationConfig(
              objectMapper.writeValueAsString(submitJobRequest.getNotificationConfig()));
        JobStatus jobStatus = job.getJobStatus();
        job.setJobStatus(JobStatus.ACTIVE);
        JobMaster savedJob = jobMasterRepository.save(job);
        if (jobStatus.equals(JobStatus.PAUSED)) {
          jobPauseResume(new JobActionRequest(savedJob.getJobName(), JobActions.RESUME), actionBy);
        }
        log.info("activated job: {}", savedJob);
        return savedJob;
      } catch (Exception e) {
        throw new StitchMateGenericException(e.getMessage());
      }
    }
    throw new StitchMateGenericException(
        String.format(
            "No job exists with valid status for jobId: %s", submitJobRequest.getJobId()));
  }

  public JobDetails getJobDetails(Long jobId) {
    if (validationService.checkIfJobIdExists(jobId)) {
      List<EdgeList> edgeListList = edgeMasterRepository.getEdgeListByJobId(jobId);
      List<NodeList> nodeListList = nodeMasterRepository.getNodeListByJobId(jobId);
      List<TagList> tags = tagMasterRepository.getJobTags(jobId);
      JobMaster jobMaster = jobMasterRepository.getByJobId(jobId);
      JobDetails jobDetails = new JobDetails();
      jobDetails.setJob(jobMaster);
      jobDetails.setEdges(edgeListList);
      jobDetails.setNodes(nodeListList);
      jobDetails.setTags(tags);
      log.info("fetched job details {}", jobDetails);
      return jobDetails;
    }
    throw new StitchMateGenericException(String.format("Job with Id: %s does not exists", jobId));
  }

  public JobRunDetails getJobRunDetails(Long jobId, String jobRunId) {
    if (validationService.checkIfJobIdExists(jobId)) {
      List<EdgeList> edgeListList = edgeMasterRepository.getEdgeListByJobId(jobId);
      JobMaster jobMaster = jobMasterRepository.getByJobId(jobId);
      List<NodeList> nodeLists = nodeMasterRepository.getNodeListByJobId(jobId);
      List<NodeRunsList> nodeRunsLists =
          nodeRunsRepository.getNodeRunsListByDagRunId(jobRunId, jobId);
      JobRunDetails jobRunDetails = new JobRunDetails();
      jobRunDetails.setJob(jobMaster);
      jobRunDetails.setEdges(edgeListList);
      jobRunDetails.setNodes(StitchUtils.getRecentRunDetails(nodeRunsLists, nodeLists));
      log.info("fetched job run details {}", jobRunDetails);
      return jobRunDetails;
    }
    throw new StitchMateGenericException(String.format("Job with Id: %s does not exists", jobId));
  }

  public JobMaster jobPauseResume(JobActionRequest jobActionRequest, String actionBy) {
    try {
      int i = 0;
      JobMaster jobResponse = null;
      AirflowActionBodyRequest airflowActionBody =
          new AirflowActionBodyRequest(jobActionRequest.getJobActions().status());
      StringBuilder jobPatchUrl = new StringBuilder();
      jobPatchUrl
          .append(airflowApiUrl)
          .append(Constants.dagEndpoint)
          .append(jobActionRequest.getJobName());
      boolean callResponse = HttpUtils.patchRequest(airflowActionBody, jobPatchUrl.toString());
      JobMaster jobMaster = jobMasterRepository.getByJobName(jobActionRequest.getJobName());
      if (jobActionRequest.getJobActions().status() && callResponse) {
        jobMaster.setJobStatus(JobStatus.PAUSED);
        notificationService.sendSlackAlertWithAttachment(
            jobMaster.getJobId(), JobStatus.PAUSED.name(), actionBy);
        log.info("{} job Paused", jobActionRequest.getJobName());
      } else if (!(jobActionRequest.getJobActions().status()) && callResponse) {
        jobMaster.setJobStatus(JobStatus.ACTIVE);
        notificationService.sendSlackAlertWithAttachment(
            jobMaster.getJobId(), JobStatus.ACTIVE.name(), actionBy);
        log.info("{} job Resumed", jobActionRequest.getJobName());
      } else {
        log.error("failed while calling airflow api");
        throw new Exception("Http Call failed:" + jobActionRequest.getJobName());
      }
      while (i < 3 && jobResponse == null) {
        try {
          jobResponse = jobMasterRepository.save(jobMaster);
          i++;
        } catch (Exception e) {
          i++;
          log.error("Failed while saving jobmaster in database, Retry attempt: {}", i);
        }
      }
      if (jobResponse == null) {
        airflowActionBody.set_paused(!jobActionRequest.getJobActions().status());
        HttpUtils.patchRequest(airflowActionBody.toString(), jobPatchUrl.toString());
        throw new StitchMateGenericException("Error Occured while Dealing with Database");
      }
      log.info("job response for Actions {}", jobResponse);
      return jobResponse;
    } catch (Exception e) {
      log.error("Failed while performing job actions");
      throw new StitchMateGenericException(
          "Internal error occurred while Pausing/Resuming the Job Message - " + e.getMessage());
    }
  }

  public List<DagRun> getJobRuns(String jobName) {
    try {
      DagRunsResponse dagRunsResponse =
          objectMapper.readValue(
              HttpUtils.get(
                  airflowApiUrl
                      + Constants.dagEndpoint
                      + jobName
                      + Constants.dagRunsEndPointOrderBy,
                  authorization,
                  session),
              DagRunsResponse.class);
      int endIndex = Math.min(dagRunsResponse.getDagRuns().size(), 10);
      List<DagRun> dagruns =
          dagRunsResponse.getDagRuns().subList(0, endIndex).stream()
              .map(StitchUtils::setStatusUpperCase)
              .collect(Collectors.toList());
      log.info("fetched job runs {}", dagruns);
      return dagruns;
    } catch (Exception e) {
      log.error("Failed while fetching job runs");
      throw new StitchMateGenericException(
          "Internal error occurred while getting dagRuns - " + e.getMessage());
    }
  }

  public SearchJobListResponse getSearch(SearchJobRequest searchJobRequest) {
    HashMap<String, Object> searchData;
    List<JobMaster> jobMasterListing;
    List<JobListResponse> responseList = new ArrayList<>();
    Long totalJobCount;
    searchData = jobMasterCustom.findAllWithFilters(searchJobRequest);
    jobMasterListing = (List<JobMaster>) searchData.get("searchList");
    totalJobCount = (Long) searchData.get("jobCount");
    SearchJobListResponse searchJobListResponse = new SearchJobListResponse();
    for (JobMaster job : jobMasterListing) {
      JobListResponse jobListResponse = new JobListResponse();
      List<DagRuns> dagRuns = getAllJobRunsFromDB(job.getJobName());
      if (job.getJobStatus().equals(JobStatus.RUNNING)
          || job.getJobStatus().equals(JobStatus.ACTIVE)
          || job.getJobStatus().equals(JobStatus.FAILED)) {
        jobListResponse.setNextDagRun(getNextRunFromDB(job.getJobName()) + " UTC");
      }
      jobListResponse.setDagRuns(dagRuns);
      jobListResponse.setJobMaster(job);
      responseList.add(jobListResponse);
    }
    searchJobListResponse.setStatus("SUCCESS");
    searchJobListResponse.setMessage("Listing page for stitch jobs");
    searchJobListResponse.setData(responseList);
    searchJobListResponse.setJobsListingPageDetail(searchJobRequest.getJobsListingPageDetail());
    searchJobListResponse.setTotalJobs(totalJobCount);
    return searchJobListResponse;
  }

  public void addTagToJob(AddRemoveTagRequest addRemoveTagRequest) {
    if (!validationService.checkIfJobIdExists(addRemoveTagRequest.getJobId())) {
      log.error("invalid job id for adding tag: {}", addRemoveTagRequest.getJobId());
      throw new StitchMateBadRequestException("Invalid Job Id : " + addRemoveTagRequest.getJobId());
    }
    if (!validationService.checkIfTagExists(addRemoveTagRequest.getTagId())) {
      log.error("invalid tag id for adding tag: {}", addRemoveTagRequest.getTagId());
      throw new StitchMateBadRequestException(
          "One or more Invalid tags : " + addRemoveTagRequest.getTagId());
    }
    addRemoveTagRequest
        .getTagId()
        .forEach(
            tagId -> {
              if (!validationService.checkIfTagJobMappingExists(
                  addRemoveTagRequest.getJobId(), tagId)) {
                JobsTagsRelations jobsTagsRelations = new JobsTagsRelations();
                jobsTagsRelations.setJobId(addRemoveTagRequest.getJobId());
                jobsTagsRelations.setTagId(tagId);
                if (Objects.nonNull(addRemoveTagRequest.getUser())) {
                  jobsTagsRelations.setCreatedBy(addRemoveTagRequest.getUser());
                }
                jobsTagsRelationsRepository.save(jobsTagsRelations);
                log.info("tag saved: {}", jobsTagsRelations);
              } else {
                log.error("tag is already associated");
                throw new StitchMateBadRequestException(
                    "The given tag is already associated with job");
              }
            });
  }

  public void removeTagFromJob(AddRemoveTagRequest addRemoveTagRequest) {
    if (!validationService.checkIfJobIdExists(addRemoveTagRequest.getJobId())) {
      log.error("invalid job id for tag removal: {}", addRemoveTagRequest.getJobId());
      throw new StitchMateBadRequestException("Invalid Job Id : " + addRemoveTagRequest.getJobId());
    }
    if (!validationService.checkIfTagExists(addRemoveTagRequest.getTagId())) {
      log.error("invalid tag id for tag removal: {}", addRemoveTagRequest.getTagId());
      throw new StitchMateBadRequestException(
          "One or more Invalid tags : " + addRemoveTagRequest.getTagId());
    }
    addRemoveTagRequest
        .getTagId()
        .forEach(
            tagId -> {
              if (validationService.checkIfTagJobMappingExists(
                  addRemoveTagRequest.getJobId(), tagId)) {
                jobsTagsRelationsRepository.deleteByJobIdAndAndTagId(
                    addRemoveTagRequest.getJobId(), tagId);
                log.info("tag removed for id: {}", addRemoveTagRequest.getTagId());
              } else {
                log.error("cannot find given tag to delete");
                throw new StitchMateBadRequestException("The given tag is not associated with job");
              }
            });
  }

  public JobMaster updateJob(UpdateJobRequest updateJobRequest) {
    if (!validationService.checkIfJobIdExists(updateJobRequest.getJobId())) {
      log.error("Invalid job id for update: {}", updateJobRequest.getJobId());
      throw new StitchMateGenericException("Invalid Job Id : " + updateJobRequest.getJobId());
    }
    if (validationService.checkIfJobIdIsExpired(updateJobRequest.getJobId())) {
      log.error("Expired Job: {}", updateJobRequest.getJobId());
      throw new StitchMateGenericException("Expired Job: " + updateJobRequest.getJobId());
    }
    if (Objects.nonNull(updateJobRequest.getEndDate())
        && !validationService.isFutureDate(updateJobRequest.getEndDate())) {
      log.error("Invalid Date, date has to be greater than: {}", LocalDateTime.now());
      throw new StitchMateGenericException(
          "Invalid Date, date has to be greater than : " + LocalDateTime.now());
    }
    JobMaster jobMaster = jobMasterRepository.getByJobId(updateJobRequest.getJobId());
    if (Objects.nonNull(updateJobRequest.getEndDate())) {
      jobMaster.setEndDate(updateJobRequest.getEndDate());
    }
    if (Objects.nonNull(updateJobRequest.getDescription())) {
      jobMaster.setDescription(updateJobRequest.getDescription());
    }
    log.info("job updated {}", jobMaster);
    return jobMasterRepository.save(jobMaster);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public JobMaster deleteJob(DeleteJobRequest deleteJobRequest, String deletedBy) {

    if (!validationService.checkIfJobIdExists(deleteJobRequest.getJobId())) {
      log.error("Invalid job id for delete: {}", deleteJobRequest.getJobId());
      throw new StitchMateGenericException("Invalid Job Id : " + deleteJobRequest.getJobId());
    }
    JobMaster jobMaster = jobMasterRepository.getByJobId(deleteJobRequest.getJobId());
    List<NodeMaster> nodeList = nodeMasterRepository.findByJobId(deleteJobRequest.getJobId());
    List<EdgeMaster> edgeList = edgeMasterRepository.findByJobId(deleteJobRequest.getJobId());
    if (Objects.nonNull(jobMaster)) {
      jobMaster.setIsDeleted(deleteJobRequest.getIsDelete());
      jobMaster.setDeletedBy(deletedBy);
      jobMasterRepository.save(jobMaster);
    }
    if (Objects.nonNull(nodeList)) {
      nodeList
          .parallelStream()
          .forEach(
              (nodeMaster) -> {
                nodeMaster.setDeleted(deleteJobRequest.getIsDelete());
                nodeMasterRepository.save(nodeMaster);
              });
    }
    if (Objects.nonNull(edgeList)) {
      edgeList
          .parallelStream()
          .forEach(
              (edgeMaster) -> {
                edgeMaster.setDeleted(deleteJobRequest.getIsDelete());
                edgeMasterRepository.save(edgeMaster);
              });
    }

    log.info("job deleted {}", jobMaster);
    notificationService.sendSlackAlertWithAttachment(
        deleteJobRequest.getJobId(), "Deleted", deletedBy);

    return jobMaster;
  }

  public List<DagRuns> getAllJobRunsFromDB(String jobName) {
    try {
      List<DagRuns> dagRunsList;
      if (profile.equals("uat")) {
        dagRunsList = dagRunsRepository.getByDagIdUat(jobName);
      } else {
        dagRunsList = dagRunsRepository.getByDagId(jobName);
      }
      return dagRunsList;
    } catch (Exception e) {
      log.error("Failed while fetching job runs");
      throw new StitchMateGenericException(
          "Internal error occurred while getting dagRuns - " + e.getMessage());
    }
  }

  public String getNextRunFromDB(String jobName) {
    try {
      Dags dags;
      if (profile.equals("uat")) {
        dags = dagsRepository.getByDagIdUat(jobName);
      } else {
        dags = dagsRepository.getByDagId(jobName);
      }

      return dags != null ? dags.getNextDagRun() : null;
    } catch (Exception e) {
      log.error("Failed while fetching job runs");
      throw new StitchMateGenericException(
          "Internal error occurred while getting dagRuns - " + e.getMessage());
    }
  }
}
