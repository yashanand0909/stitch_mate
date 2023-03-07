package com.mate.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mate.exceptions.StitchMateGenericException;
import com.mate.models.entities.*;
import com.mate.models.enums.JobActions;
import com.mate.models.enums.JobStatus;
import com.mate.models.requests.filter.FilterRequest;
import com.mate.models.requests.job.*;
import com.mate.models.responses.airflow.DagRun;
import com.mate.models.responses.airflow.DagRunsResponse;
import com.mate.models.responses.job.JobDetails;
import com.mate.models.responses.job.JobListResponse;
import com.mate.models.responses.job.SearchJobListResponse;
import com.mate.repositories.*;
import com.mate.repositories.custom.JobMasterCustomImpl;
import com.mate.repositories.projections.*;
import com.mate.utilities.HttpUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpUtils.class})
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
public class TestJobService {;

  CreateJobRequest createJobRequest;
  JobMaster jobMaster;

  TagMaster tagMaster;

  @Before
  public void setup() {
    PowerMockito.mockStatic(HttpUtils.class);
    ArrayList<Long> tags = new ArrayList<>();
    tags.add(7L);
    tags.add(8L);
    tags.add(9L);
    createJobRequest = new CreateJobRequest();
    createJobRequest.setJobName("Test-Job");
    createJobRequest.setDescription("Test Job  Description");
    createJobRequest.setTags(tags);
    jobMaster = new JobMaster("TestJob", "TestUser", "TestDescription");
    jobMaster.setJobId(1);
    tagMaster = new TagMaster();
    tagMaster.setTagId(1);
  }

  @Mock JobMasterRepository jobMasterRepository;

  @Mock TagMasterRepository tagMasterRepository;

  @Mock NodeMasterRepository nodeMasterRepository;

  @Mock EdgeMasterRepository edgeMasterRepository;
  @Mock ValidationService validationService;
  @Mock JobMasterCustomImpl jobMasterCustom;
  @Mock DagRunsRepository dagRunsRepository;

  @Mock EdgeList edgeList;
  @Mock EdgeList edgeList1;
  @Mock NodeList nodeList;
  @Mock NodeList nodeList1;
  @Mock JobNodeList jobNodeList;
  @Mock ObjectMapper objectMapper;
  @Mock ConfigService configService;

  @Mock NotificationService notificationService;
  @InjectMocks JobService jobService;

  @Test
  public void createJobTestSuccess() {
    when(jobMasterRepository.save(any(JobMaster.class))).thenReturn(jobMaster);
    assert jobMaster.equals(jobService.createJob(createJobRequest, "TestUser"));
  }

  @Test
  @SneakyThrows
  public void testCreateJobTestFailedInvalidJobName() {
    when(validationService.checkIfJobNameExists(anyString())).thenReturn(true);
    assertThrows(
        StitchMateGenericException.class, () -> jobService.createJob(createJobRequest, "TestUser"));
  }

  @Test
  public void testGetJobDetails() {

    List<EdgeList> listOfEdges = new ArrayList<>();
    listOfEdges.add(edgeList);
    listOfEdges.add(edgeList1);
    List<NodeList> listOfNodes = new ArrayList<>();
    listOfNodes.add(nodeList);
    listOfNodes.add(nodeList1);
    JobDetails jobDetails = new JobDetails();
    jobDetails.setNodes(listOfNodes);
    jobDetails.setEdges(listOfEdges);
    jobDetails.setJob(jobMaster);
    jobDetails.setTags(new ArrayList<TagList>());
    when(validationService.checkIfJobIdExists(1L)).thenReturn(true);
    when(edgeMasterRepository.getEdgeListByJobId(any(Long.class))).thenReturn(listOfEdges);
    when(nodeMasterRepository.getNodeListByJobId(any(Long.class))).thenReturn(listOfNodes);
    when(jobMasterRepository.getByJobId(any(Long.class))).thenReturn(jobMaster);
    when(tagMasterRepository.getJobTags(any(Long.class))).thenReturn(new ArrayList<TagList>());
    assert jobDetails.equals(jobService.getJobDetails(1L));
  }

  @Test
  public void testGetJobDetailsWithInvalidJobId() {
    assertThrows(StitchMateGenericException.class, () -> jobService.getJobDetails(2L));
  }

  @Test
  public void testActivateJob() {
    SubmitJobRequest submitJobRequest = new SubmitJobRequest();
    submitJobRequest.setJobId(1L);
    List<JobNodeList> list = new ArrayList<>();
    list.add(jobNodeList);
    Optional<JobMaster> jobMaster1 = Optional.of(jobMaster);
    when(validationService.checkIfJobIdExistsWithValidStatus(1L)).thenReturn(true);
    when(nodeMasterRepository.findAllNodeOfJob(1L)).thenReturn(list);
    when(jobMasterRepository.findById(1L)).thenReturn(jobMaster1);
    assert jobMaster.equals(jobService.activateJob(submitJobRequest, "Test"));
  }

  @Test
  public void testActivateJobJobIdNotFound() {
    SubmitJobRequest submitJobRequest = new SubmitJobRequest();
    submitJobRequest.setJobId(1L);
    when(validationService.checkIfJobIdExistsWithValidStatus(1L)).thenReturn(false);
    assertThrows(
        StitchMateGenericException.class, () -> jobService.activateJob(submitJobRequest, "Test"));
  }

  @Test
  public void testActivateJobShouldFailWhenNoNodesAreSet() {
    SubmitJobRequest submitJobRequest = new SubmitJobRequest();
    submitJobRequest.setJobId(1L);
    List<JobNodeList> list = new ArrayList<>();
    when(validationService.checkIfJobIdExistsWithValidStatus(1L)).thenReturn(true);
    when(nodeMasterRepository.findAllNodeOfJob(1L)).thenReturn(list);
    assertThrows(
        StitchMateGenericException.class, () -> jobService.activateJob(submitJobRequest, "Test"));
  }

  @Test
  @SneakyThrows
  public void testJobPauseResumeThrows() {
    JobActionRequest jobActionRequest = new JobActionRequest("TestJob", JobActions.PAUSE);
    jobActionRequest.setJobId(1l);
    when(HttpUtils.patchRequest(any(AirflowActionBodyRequest.class), any(String.class)))
        .thenReturn(false);
    jobMaster.setJobStatus(JobStatus.PAUSED);
    jobMaster.setBackfill(true);
    jobMaster.setStartDate(LocalDateTime.of(2022, 1, 1, 1, 1));
    jobMaster.setEndDate(LocalDateTime.of(2023, 1, 1, 1, 1));
    jobMaster.setSchedule("@daily");
    jobMaster.setLastExecutionId(123);
    jobMaster.setStartNode("1");
    jobMaster.setCreatedAt(LocalDateTime.of(2022, 1, 1, 1, 1));
    jobMaster.setRecupdatedAt(LocalDateTime.of(2022, 1, 1, 1, 1));
    when(jobMasterRepository.getByJobId(any(long.class))).thenReturn(jobMaster);
    when(jobMasterRepository.save(any(JobMaster.class))).thenReturn(jobMaster);
    assertThrows(Exception.class, () -> jobService.jobPauseResume(jobActionRequest, ""));
  }

  @Test
  @SneakyThrows
  public void testJobPauseResumeDatabaseIssue() {
    JobActionRequest jobActionRequest = new JobActionRequest("TestJob", JobActions.PAUSE);
    jobActionRequest.setJobId(1l);
    when(HttpUtils.patchRequest(any(AirflowActionBodyRequest.class), any(String.class)))
        .thenReturn(true);
    jobMaster.setJobStatus(JobStatus.PAUSED);
    jobMaster.setBackfill(true);
    jobMaster.setStartDate(LocalDateTime.of(2022, 1, 1, 1, 1));
    jobMaster.setEndDate(LocalDateTime.of(2023, 1, 1, 1, 1));
    jobMaster.setSchedule("@daily");
    jobMaster.setLastExecutionId(123);
    jobMaster.setStartNode("1");
    jobMaster.setCreatedAt(LocalDateTime.of(2022, 1, 1, 1, 1));
    jobMaster.setRecupdatedAt(LocalDateTime.of(2022, 1, 1, 1, 1));
    when(jobMasterRepository.getByJobId(any(long.class))).thenReturn(jobMaster);
    when(jobMasterRepository.save(any(JobMaster.class))).thenReturn(null);
    assertThrows(
        StitchMateGenericException.class, () -> jobService.jobPauseResume(jobActionRequest, ""));
  }

  @Test
  @SneakyThrows
  public void testGetJobsRuns() {
    DagRun dagRun = new DagRun();
    dagRun.setStatus("SUCCESS");
    dagRun.setDagRunId("Test");
    List<DagRun> dagRuns = new ArrayList<>(Collections.singletonList(dagRun));
    DagRunsResponse response = new DagRunsResponse();
    response.setDagRuns(dagRuns);
    when(HttpUtils.get(any(), any(), any())).thenReturn("TEST");
    when(objectMapper.readValue(anyString(), eq(DagRunsResponse.class))).thenReturn(response);
    List<DagRun> returnValue = jobService.getJobRuns("test");
    assert returnValue.size() == 1;
    assert "Test".equals(returnValue.get(0).getDagRunId());
    int n = 0;
    while (n < 15) {
      response.getDagRuns().add(dagRun);
      n++;
    }

    when(objectMapper.readValue(anyString(), eq(DagRunsResponse.class))).thenReturn(response);
    returnValue = jobService.getJobRuns("test");
    assert returnValue.size() == 10;
  }

  @Test
  @SneakyThrows
  public void testSearchJob() {
    ReflectionTestUtils.setField(jobService, "profile", "uat");
    SearchJobListResponse searchJobListResponse = new SearchJobListResponse();
    JobsListingPageDetail jobsListingPageDetail = new JobsListingPageDetail();
    List<JobListResponse> responseList = new ArrayList<>();
    SearchJobRequest searchJobRequest = new SearchJobRequest();
    FilterRequest filterRequest = new FilterRequest();
    filterRequest.setStatus(JobStatus.RUNNING);
    searchJobRequest.setJobName("TestJob");
    searchJobRequest.setFilterRequest(filterRequest);
    List<JobMaster> listJobMaster = new ArrayList<>();
    JobMaster jobMaster = new JobMaster();
    jobMaster.setJobName("TestJob");
    jobMaster.setJobStatus(JobStatus.RUNNING);
    jobMaster.setBackfill(true);
    jobMaster.setStartDate(LocalDateTime.of(2022, 1, 1, 12, 0, 0));
    jobMaster.setEndDate(LocalDateTime.of(2023, 1, 1, 12, 0, 0));
    jobMaster.setSchedule("@daily");
    jobMaster.setLastExecutionId(123);
    jobMaster.setStartNode("1");
    jobMaster.setCreatedAt(LocalDateTime.of(2022, 1, 1, 1, 1));
    jobMaster.setRecupdatedAt(LocalDateTime.of(2022, 1, 1, 1, 1));
    listJobMaster.add(jobMaster);
    HashMap<String, Object> searchData = new HashMap<>();
    searchData.put("searchList", listJobMaster);
    searchData.put("jobCount", 1L);
    when(jobMasterCustom.findAllWithFilters(any(SearchJobRequest.class))).thenReturn(searchData);
    JobListResponse jobListResponse = new JobListResponse();
    List<DagRuns> listDagRuns = new ArrayList<>();
    DagRuns dagRun = new DagRuns();
    dagRun.setStatus("RUNNING");
    dagRun.setDagId("0");
    dagRun.setRunId("1");
    dagRun.setStartDate("");
    dagRun.setEndDate("");
    dagRun.setExecutionDate(LocalDateTime.of(2022, 1, 1, 1, 1));
    listDagRuns.add(dagRun);
    List<DagRuns> dagRuns = new ArrayList<>(Collections.singletonList(dagRun));
    jobListResponse.setDagRuns(dagRuns);
    jobListResponse.setJobMaster(listJobMaster.get(0));
    responseList.add(jobListResponse);
    searchJobListResponse.setTotalJobs(1L);
    searchJobListResponse.setData(responseList);
    searchJobListResponse.setStatus("SUCCESS");
    searchJobListResponse.setMessage("Listing page for stitch jobs");
    searchJobListResponse.setJobsListingPageDetail(jobsListingPageDetail);
    when(dagRunsRepository.getByDagIdUat(any()))
        .thenReturn(searchJobListResponse.getData().get(0).getDagRuns());
    assert searchJobListResponse.getData().equals(jobService.getSearch(searchJobRequest).getData());
  }

  @Test
  public void testUpdateJob() {
    LocalDateTime now = LocalDateTime.now().plusMinutes(50);
    UpdateJobRequest updateJobRequest = new UpdateJobRequest();
    updateJobRequest.setJobId(10L);
    updateJobRequest.setEndDate(now);

    JobMaster jobMaster1 = new JobMaster("TestJob", "TestUser", "TestDescription");
    jobMaster1.setJobId(10);
    jobMaster1.setEndDate(LocalDateTime.now());

    JobMaster jobMaster2 = new JobMaster("TestJob", "TestUser", "TestDescription");
    jobMaster1.setJobId(10);
    jobMaster1.setEndDate(now);

    when(validationService.checkIfJobIdExists(10L)).thenReturn(false);
    assertThrows(StitchMateGenericException.class, () -> jobService.updateJob(updateJobRequest));

    when(validationService.isFutureDate(LocalDateTime.now())).thenReturn(false);
    assertThrows(StitchMateGenericException.class, () -> jobService.updateJob(updateJobRequest));

    when(validationService.checkIfJobIdExists(10L)).thenReturn(true);
    when(validationService.isFutureDate(any(LocalDateTime.class))).thenReturn(true);
    when(jobMasterRepository.findByJobIdAndJobStatusIsNotAndIsDeletedIsFalse(
            any(Long.class), any(JobStatus.class)))
        .thenReturn(jobMaster2);
    when(jobMasterRepository.getByJobId(any(Long.class))).thenReturn(jobMaster1);
    when(jobMasterRepository.save(jobMaster1)).thenReturn(jobMaster2);
    assert jobMaster2.equals(jobService.updateJob(updateJobRequest));
  }

  @Test
  public void testDeleteJob() {
    DeleteJobRequest deleteJobRequest = new DeleteJobRequest();
    deleteJobRequest.setJobId(10L);
    deleteJobRequest.setIsDelete(true);
    JobMaster jobMaster1 = new JobMaster("TestJob", "TestUser", "TestDescription");
    NodeMaster nodeMaster = new NodeMaster();
    nodeMaster.setJobId(10L);
    List<NodeMaster> nodeMasterList = new ArrayList<>();
    nodeMasterList.add(0, nodeMaster);
    List<EdgeMaster> edgeMasterList = new ArrayList<>();
    EdgeMaster edgeMaster = new EdgeMaster();
    edgeMaster.setJobId(10L);
    edgeMasterList.add(0, edgeMaster);
    when(validationService.checkIfJobIdExists(10L)).thenReturn(false);
    assertThrows(
        StitchMateGenericException.class,
        () -> jobService.deleteJob(deleteJobRequest, "mehul.batra@dream11.com"));

    when(validationService.checkIfJobIdExists(10L)).thenReturn(true);
    when(jobMasterRepository.getByJobId(any(Long.class))).thenReturn(jobMaster);
    when(nodeMasterRepository.findByJobId(any(Long.class))).thenReturn(nodeMasterList);
    when(edgeMasterRepository.findByJobId(any(Long.class))).thenReturn(edgeMasterList);

    jobMaster1.setJobId(10L);
    jobMaster1.setIsDeleted(true);
    jobMaster1.setDeletedBy("mehul.batra@dream11.com");
    when(jobMasterRepository.save(any(JobMaster.class))).thenReturn(jobMaster1);
    nodeMasterList.get(0).setDeleted(true);
    when(nodeMasterRepository.save(any(NodeMaster.class))).thenReturn(nodeMaster);
    edgeMasterList.get(0).setDeleted(true);
    when(edgeMasterRepository.save(any(EdgeMaster.class))).thenReturn(edgeMaster);
    notificationService.sendSlackAlertWithAttachment(10L, "Deleted", "Test");

    assert jobMaster1
        .getIsDeleted()
        .equals(
            jobService.deleteJob(deleteJobRequest, "mehul" + ".batra@dream11.com").getIsDeleted());
  }
}
