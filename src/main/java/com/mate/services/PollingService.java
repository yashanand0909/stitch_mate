package com.mate.services;

import static com.mate.constants.Constants.onceCron;

import com.mate.models.entities.JobMaster;
import com.mate.models.entities.NodeMaster;
import com.mate.models.enums.JobStatus;
import com.mate.models.responses.airflow.DagRun;
import com.mate.repositories.JobMasterRepository;
import com.mate.repositories.NodeMasterRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@EnableScheduling
public class PollingService {

  private final JobMasterRepository jobMasterRepository;

  private final JobService jobService;

  private final NotificationService notificationService;

  private final NodeMasterRepository nodeMasterRepository;

  public PollingService(
      JobMasterRepository jobMasterRepository,
      JobService jobService,
      NotificationService notificationService,
      NodeMasterRepository nodeMasterRepository) {
    this.jobMasterRepository = jobMasterRepository;
    this.jobService = jobService;
    this.notificationService = notificationService;
    this.nodeMasterRepository = nodeMasterRepository;
  }

  @Scheduled(fixedDelay = 60000)
  public void checkRequestStatus() {
    List<JobMaster> requestQueryRelationList =
        jobMasterRepository.findByJobStatus(JobStatus.RUNNING);
    requestQueryRelationList.forEach(this::checkAndUpdateStatus);
    List<JobMaster> expiredJobs =
        jobMasterRepository.findAllByEndDateBeforeAndJobStatusIsNotIn(
            LocalDateTime.now(), Arrays.asList(JobStatus.EXPIRED, JobStatus.RUNNING));
    expiredJobs.forEach(this::setJobAsExpired);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void setJobAsExpired(JobMaster jobMaster) {
    log.info("Setting job " + jobMaster.getJobId() + " as EXPIRED");
    jobMaster.setJobStatus(JobStatus.EXPIRED);
    jobMasterRepository.save(jobMaster);
    log.info(
        "current status: {} set for job - {}", jobMaster.getJobStatus(), jobMaster.getJobName());
    List<NodeMaster> nodeMasters = nodeMasterRepository.findByJobId(jobMaster.getJobId());
    for (NodeMaster node : nodeMasters) {
      node.setDeleted(true);
      nodeMasterRepository.save(node);
      log.info("current node deleted: {} ,as Job expired", node.getNodeId());
    }
    notificationService.sendSlackAlertWithAttachment(
        jobMaster.getJobId(), JobStatus.EXPIRED.name(), "");
  }

  private void checkAndUpdateStatus(JobMaster jobMaster) {
    try {
      List<DagRun> dagRunList = jobService.getJobRuns(jobMaster.getJobName());
      if (dagRunList.size() != 0) {
        if (dagRunList.get(0).getStatus().equals("SUCCESS")
            || dagRunList.get(0).getStatus().equals("FAILED")) {
          jobMaster.setJobStatus(JobStatus.ACTIVE);
          if (dagRunList.get(0).getStatus().equals("FAILED")) {
            jobMaster.setJobStatus(JobStatus.FAILED);
          }
          if (dagRunList.get(0).getStatus().equals("SUCCESS")
              && jobMaster.getSchedule().equals(onceCron)) {
            setJobAsExpired(jobMaster);
          }
          jobMasterRepository.save(jobMaster);
          if (jobMaster.getJobStatus().equals(JobStatus.FAILED)) {
            notificationService.sendSlackAlertWithAttachment(
                jobMaster.getJobId(), dagRunList.get(0).getStatus(), "");
          }
          log.info(
              "current status: {} set for job - {}",
              jobMaster.getJobStatus(),
              jobMaster.getJobName());
        }
      }
    } catch (Exception e) {
      log.error("failed to update the job status {}", e.getMessage());
    }
  }
}
