package com.mate.repositories;

import com.mate.models.entities.TagMaster;
import com.mate.repositories.projections.TagList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagMasterRepository extends JpaRepository<TagMaster, Long> {

  TagMaster findTagMasterByTag(String tag);

  @Query("select t.tag as tag, t.tagId as tagId from TagMaster t order by t.tag asc  ")
  List<TagList> getTagList();

  @Query(
      value =
          "select t.tag as tag, t.tagId as tagId from TagMaster t where t.tagId in (select j.tagId from JobsTagsRelations j where j.jobId = :id)")
  List<TagList> getJobTags(long id);
}
