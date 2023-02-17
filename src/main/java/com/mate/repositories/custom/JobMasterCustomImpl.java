package com.mate.repositories.custom;

import com.mate.models.entities.JobMaster;
import com.mate.models.entities.NodeMaster;
import com.mate.models.enums.JobStatus;
import com.mate.models.requests.job.SearchJobRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class JobMasterCustomImpl {

  private final EntityManager entityManager;
  private final CriteriaBuilder criteriaBuilder;

  @Autowired
  public JobMasterCustomImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
    this.criteriaBuilder = entityManager.getCriteriaBuilder();
  }

  public HashMap<String, Object> findAllWithFilters(SearchJobRequest searchJobRequest) {
    HashMap<String, Object> searchData = new HashMap<>();
    CriteriaQuery<JobMaster> criteriaQuery = criteriaBuilder.createQuery(JobMaster.class);
    Root<JobMaster> jobMasterRoot = criteriaQuery.from(JobMaster.class);
    Join<JobMaster, NodeMaster> join = jobMasterRoot.join("jobIds", JoinType.LEFT);
    Predicate predicate = getPredicate(searchJobRequest, jobMasterRoot, join);
    criteriaQuery.where(predicate).distinct(true);
    setOrder(searchJobRequest, criteriaQuery, jobMasterRoot);
    TypedQuery<JobMaster> typedQuery = entityManager.createQuery(criteriaQuery);
    Long jobCount = typedQuery.getResultList().stream().distinct().count();
    typedQuery.setFirstResult(
        searchJobRequest.getJobsListingPageDetail().getPageNumber()
            * searchJobRequest.getJobsListingPageDetail().getPageSize());
    typedQuery.setMaxResults(searchJobRequest.getJobsListingPageDetail().getPageSize());
    typedQuery.getResultList().stream().distinct();
    searchData.put("searchList", typedQuery.getResultList());
    searchData.put("jobCount", jobCount);
    return searchData;
  }

  private Predicate getPredicate(
      SearchJobRequest searchJobRequest,
      Root<JobMaster> jobMasterRoot,
      Join<JobMaster, NodeMaster> join) {
    boolean isFilter = false;
    List<Predicate> predicates = new ArrayList<>();
    if (searchJobRequest.getJobName() != null && !searchJobRequest.getJobName().equals("")) {
      isFilter = true;
      predicates.add(
          criteriaBuilder.like(
              jobMasterRoot.get("jobName"), "%" + searchJobRequest.getJobName() + "%"));
      log.info("criteria added for jobName");
    }
    if (searchJobRequest.getTableName() != null && !searchJobRequest.getTableName().equals("")) {
      isFilter = true;
      predicates.add(
          criteriaBuilder.like(
              join.get("destinationTable"), "%" + searchJobRequest.getTableName() + "%"));
      log.info("criteria added for destinationTable");
    }
    if (Objects.nonNull(searchJobRequest.getFilterRequest())
        && searchJobRequest.getFilterRequest().getCreatedBy() != null
        && !searchJobRequest.getFilterRequest().getCreatedBy().equals("")) {
      isFilter = true;
      predicates.add(
          criteriaBuilder.equal(
              jobMasterRoot.get("createdBy"), searchJobRequest.getFilterRequest().getCreatedBy()));
      log.info("criteria added for createdBy");
    }
    if (Objects.nonNull(searchJobRequest.getFilterRequest())
        && searchJobRequest.getFilterRequest().getStatus() != null) {
      isFilter = true;
      predicates.add(
          criteriaBuilder.equal(
              jobMasterRoot.get("jobStatus"), searchJobRequest.getFilterRequest().getStatus()));
      log.info("criteria added for jobStatus");
    }
    if (!isFilter) {
      predicates.add(criteriaBuilder.notEqual(jobMasterRoot.get("jobStatus"), JobStatus.EXPIRED));
    }
    predicates.add(criteriaBuilder.equal(jobMasterRoot.get("isDeleted"), false));
    return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
  }

  private void setOrder(
      SearchJobRequest searchJobRequest,
      CriteriaQuery<JobMaster> criteriaQuery,
      Root<JobMaster> jobMasterRoot) {
    if (searchJobRequest.getJobsListingPageDetail().getSortDirection().equals(Sort.Direction.ASC)) {
      criteriaQuery.orderBy(
          criteriaBuilder.asc(
              jobMasterRoot.get(searchJobRequest.getJobsListingPageDetail().getSortBy())));
      log.info(
          "sorting search page by ASC order on {}",
          searchJobRequest.getJobsListingPageDetail().getSortBy());
    } else {
      criteriaQuery.orderBy(
          criteriaBuilder.desc(
              jobMasterRoot.get(searchJobRequest.getJobsListingPageDetail().getSortBy())));
      log.info(
          "sorting search page by DESC order on {}",
          searchJobRequest.getJobsListingPageDetail().getSortBy());
    }
  }
}
