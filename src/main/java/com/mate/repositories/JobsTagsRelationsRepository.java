package com.mate.repositories;

import com.mate.models.entities.JobsTagsRelations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobsTagsRelationsRepository extends JpaRepository<JobsTagsRelations, Long> {
  JobsTagsRelations findByJobIdAndTagId(long jobId, long tagId);

  Long deleteByJobIdAndAndTagId(long jobId, long tagId);
}
