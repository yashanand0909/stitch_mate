package com.mate.repositories;

import com.mate.models.entities.JobMaster;
import com.mate.models.enums.JobStatus;
import com.mate.repositories.projections.CreatedByList;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JobMasterRepository extends JpaRepository<JobMaster, Long> {

  JobMaster findByJobId(long jobId);

  JobMaster findByJobName(String jobName);

  JobMaster findByCreatedBy(String created_by);

  JobMaster findByJobIdAndJobName(long jobId, String jobName);

  List<JobMaster> findByJobStatus(JobStatus jobStatus);

  @Query("select distinct jobMaster.createdBy as createdBy from JobMaster jobMaster")
  List<CreatedByList> findAllCreatedBy();

  List<JobMaster> findAllByEndDateBeforeAndJobStatusIsNotIn(LocalDateTime now, List jobStatus);

  @Query(
      "select jobMaster from JobMaster jobMaster where jobMaster.jobId = :jobId and jobMaster.jobStatus in (com"
          + ".mate.models.enums.JobStatus.PAUSED,com.mate.models.enums.JobStatus.DRAFT) and jobMaster.isDeleted = false")
  JobMaster findJobMasterByJobIdWithValidStatus(Long jobId);

  @Query(
      "select jobMaster from JobMaster jobMaster where jobMaster.jobId = :jobId and jobMaster.jobName = :jobName and "
          + "jobMaster.jobStatus in (com.mate.models.enums.JobStatus.FAILED,com.mate.models.enums.JobStatus.ACTIVE,com.mate.models.enums.JobStatus"
          + ".PAUSED) and jobMaster.isDeleted = false")
  JobMaster findJobMasterByJobIdAAndJobNameWithActiveStatus(Long jobId, String jobName);

  JobMaster findByJobIdAndJobStatusIsNotAndIsDeletedIsFalse(long jobId, JobStatus jobStatus);

  @Query(
      "select jobMaster from JobMaster jobMaster where jobMaster.jobId = :jobId and jobMaster.isDeleted = false ")
  JobMaster getByJobId(Long jobId);

  @Query(
      "select jobMaster from JobMaster jobMaster where jobMaster.jobName = :jobName and jobMaster.isDeleted = false ")
  JobMaster getByJobName(String jobName);
}
