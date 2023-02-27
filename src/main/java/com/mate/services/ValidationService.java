package com.mate.services;

import com.mate.exceptions.StitchMateGenericException;
import com.mate.models.configs.destination.RedshiftConnectionConfig;
import com.mate.models.configs.queryparams.Parameter;
import com.mate.models.entities.*;
import com.mate.models.enums.JobStatus;
import com.mate.models.requests.BaseRequest;
import com.mate.models.requests.config.AddConfigRequest;
import com.mate.models.requests.edge.CreateEdgeRequest;
import com.mate.models.requests.edge.DeleteEdgeRequest;
import com.mate.models.requests.job.JobActionRequest;
import com.mate.models.requests.node.BaseNodeRequest;
import com.mate.models.requests.node.CreateNodeRequest;
import com.mate.models.requests.node.DeleteNodeRequest;
import com.mate.models.requests.node.EditNodeRequest;
import com.mate.models.responses.validation.ValidationResponse;
import com.mate.repositories.*;
import com.mate.repositories.projections.DestinationTable;
import com.mate.repositories.projections.EdgeList;
import com.mate.utilities.CyclicUtils;
import com.mate.utilities.StitchUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ValidationService {

  final EdgeMasterRepository edgeMasterRepository;
  final NodeMasterRepository nodeMasterRepository;
  final JobMasterRepository jobMasterRepository;
  final TagMasterRepository tagMasterRepository;
  final ConfigTableRepository configTableRepository;

  final JobsTagsRelationsRepository jobsTagsRelationsRepository;

  public ValidationService(
      EdgeMasterRepository edgeMasterRepository,
      NodeMasterRepository nodeMasterRepository,
      JobMasterRepository jobMasterRepository,
      TagMasterRepository tagMasterRepository,
      ConfigTableRepository configTableRepository,
      JobsTagsRelationsRepository jobsTagsRelationsRepository) {
    this.edgeMasterRepository = edgeMasterRepository;
    this.nodeMasterRepository = nodeMasterRepository;
    this.jobMasterRepository = jobMasterRepository;
    this.tagMasterRepository = tagMasterRepository;
    this.configTableRepository = configTableRepository;
    this.jobsTagsRelationsRepository = jobsTagsRelationsRepository;
  }

  public ValidationResponse validateEdgeCreationRequest(CreateEdgeRequest createEdgeRequest) {
    ValidationResponse validationResponse = new ValidationResponse(true);
    try {
      List<EdgeMaster> edgeMasterList =
          edgeMasterRepository.findByJobIdAndFromNodeAndToNode(
              createEdgeRequest.getJobId(),
              createEdgeRequest.getFromNode(),
              createEdgeRequest.getToNode());
      if (checkIfJobIdIsExpired(createEdgeRequest.getJobId())) {
        log.error("Expired Job: {}", createEdgeRequest.getJobId());
        throw new StitchMateGenericException("Expired Job: " + createEdgeRequest.getJobId());
      }
      if (edgeMasterList.size() != 0) {
        String comment = "JobId, fromNode and toNode group already exists!!";
        validationResponse =
            StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
        log.info(comment);
      }
      List<NodeMaster> nodeMasterList =
          nodeMasterRepository.findByNodeIdList(
              Arrays.asList(createEdgeRequest.getFromNode(), createEdgeRequest.getToNode()));
      if (nodeMasterList.size() < 2) {
        String comment = "FromNode or ToNode missmatch";
        validationResponse =
            StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
        validationResponse = checkIfJobExists(createEdgeRequest, validationResponse);
        log.info(comment);
      }
      if (containsCycle(createEdgeRequest)) {
        String comment = "Cyclic dependency error";
        validationResponse =
            StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
        log.info(comment);
      }
      if (validationResponse.getIsValid()) {
        String comment = "Hurray";
        validationResponse.setComment(comment);
        log.info(comment);
      }
      return validationResponse;
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new StitchMateGenericException("Error occurred while validating request");
    }
  }

  public ValidationResponse validateEdgeDeletionRequest(DeleteEdgeRequest deleteEdgeRequest) {
    ValidationResponse validationResponse = new ValidationResponse(true);
    try {
      EdgeMaster edgeMaster = edgeMasterRepository.findByEdgeId(deleteEdgeRequest.getEdgeId());
      if (checkIfJobIdIsExpired(deleteEdgeRequest.getJobId())) {
        log.error("Expired Job: {}", deleteEdgeRequest.getJobId());
        throw new StitchMateGenericException("Expired Job: " + deleteEdgeRequest.getJobId());
      }
      if (edgeMaster == null) {
        String comment = "Edge missing for given edgeId";
        validationResponse =
            StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
        log.info(comment);
      } else if (edgeMaster.isDeleted()) {
        String comment = "Edge already deleted";
        validationResponse =
            StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
        log.info(comment);
      }
      return validationResponse;
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new StitchMateGenericException("Error occurred while validating request");
    }
  }

  public ValidationResponse validateNodeDeletionRequest(DeleteNodeRequest deleteNodeRequest) {
    ValidationResponse validationResponse = new ValidationResponse(true);
    try {
      NodeMaster nodeMaster = nodeMasterRepository.findByNodeId(deleteNodeRequest.getNodeId());
      if (nodeMaster == null) {
        String comment = "Node missing for given nodeId";
        validationResponse =
            StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
        log.info(comment);
      } else if (nodeMaster.isDeleted()) {
        String comment = "Node already deleted";
        validationResponse =
            StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
        log.info(comment);
      } else if (checkIfJobIdIsExpired(deleteNodeRequest.getJobId())) {
        String comment = "Node belongs to expired Job";
        validationResponse =
            StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
        log.info(comment);
      }
      return validationResponse;
    } catch (Exception e) {
      e.printStackTrace();
      throw new StitchMateGenericException("Error occurred while validating request");
    }
  }

  public ValidationResponse checkMandatoryFieldsForCreateRequest(
      CreateNodeRequest createNodeRequest) {
    ValidationResponse validationResponse = new ValidationResponse(true);
    if (!Objects.nonNull(createNodeRequest.getJobId()))
      validationResponse =
          StitchUtils.updateValidationResponseOnFailure(validationResponse, "JobId missing");
    if (!Objects.nonNull(createNodeRequest.getJobName()))
      validationResponse =
          StitchUtils.updateValidationResponseOnFailure(validationResponse, "JobName missing");
    if (!Objects.nonNull(createNodeRequest.getSource()))
      validationResponse =
          StitchUtils.updateValidationResponseOnFailure(validationResponse, "Source missing");
    if (!Objects.nonNull(createNodeRequest.getNodeName()))
      validationResponse =
          StitchUtils.updateValidationResponseOnFailure(validationResponse, "NodeName missing");
    if (!Objects.nonNull(createNodeRequest.getQuery()))
      validationResponse =
          StitchUtils.updateValidationResponseOnFailure(validationResponse, "Query missing");
    if (!Objects.nonNull(createNodeRequest.getDestinationConfig()))
      validationResponse =
          StitchUtils.updateValidationResponseOnFailure(
              validationResponse, "Destination config missing");
    if (!Objects.nonNull(createNodeRequest.getParameterConfig()))
      validationResponse =
          StitchUtils.updateValidationResponseOnFailure(
              validationResponse, "Parameter config missing");
    return validationResponse;
  }

  public ValidationResponse checkMandatoryFieldsForUpdateRequest(EditNodeRequest editNodeRequest) {
    ValidationResponse validationResponse = new ValidationResponse(true);
    if (Objects.nonNull(editNodeRequest.getNodeName())
        && !Objects.nonNull(editNodeRequest.getJobId()))
      validationResponse =
          StitchUtils.updateValidationResponseOnFailure(
              validationResponse,
              "Job Id is a mandatory field if node name is being updated and cannot be null");
    return validationResponse;
  }

  public <T extends BaseRequest> ValidationResponse checkIfJobExists(
      T request, ValidationResponse response) {
    JobMaster jobMaster = jobMasterRepository.getByJobId(request.getJobId());
    if (jobMaster == null) {
      String comment = "No Job exists for given jobId !!";
      response = StitchUtils.updateValidationResponseOnFailure(response, comment);
      log.info(comment);
    }
    return response;
  }

  public <T extends BaseNodeRequest> ValidationResponse validateNodeRequest(T nodeRequest) {
    String destinationTable;
    ValidationResponse validationResponse = new ValidationResponse(true);
    try {
      if (Objects.nonNull(nodeRequest.getJobId())) {
        if (!checkIfJobIdExists(nodeRequest.getJobId())) {
          String comment = "Job Id Does not exist";
          validationResponse =
              StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
          log.info(comment);
        }
      }
      if (Objects.nonNull(nodeRequest.getNodeName())
          && Objects.nonNull(nodeRequest.getJobId())
          && checkifNodeExists(nodeRequest)) {
        String comment = "Node Already Exists";
        validationResponse =
            StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
        log.info(comment);
      }
      if (nodeRequest.getParameterConfig() != null) {
        if (!validateQueryParams(nodeRequest).getIsValid()) {
          String comment = "Parameters Mismatch";
          validationResponse =
              StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
          log.info(comment);
        }
      }
      if (Objects.nonNull(nodeRequest.getDestinationConfig())) {
        switch (nodeRequest.getDestinationConfig().getConnectorType()) {
          case REDSHIFT:
            RedshiftConnectionConfig connectionConfig =
                (RedshiftConnectionConfig) nodeRequest.getDestinationConfig();

            String schema = connectionConfig.getSchema();
            destinationTable = schema + "." + connectionConfig.getTableName().toLowerCase();
            boolean schemaExists =
                configTableRepository.findByConfigKey("r_schema").stream()
                    .anyMatch(configList -> configList.getConfigValue().equals(schema));
            if (!schemaExists) {
              String comment = "Schema Missmatch";
              validationResponse =
                  StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
              log.info(comment);
            }
            if (Objects.isNull(connectionConfig.getTableName())
                || Objects.isNull(connectionConfig.getSchema())) {
              String comment = "Schema.Table Missing";
              validationResponse =
                  StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
              log.info(comment);
            }
            if (checkIfDestinationTableExist(nodeRequest, destinationTable)) {
              String comment = destinationTable + " already in use";
              validationResponse =
                  StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
              log.info(comment);
            }
            break;
          default:
            break;
        }
      }
      if (checkIfJobIdIsExpired(nodeRequest.getJobId())) {
        String comment = "Cannot create/edit Node in Expired Jobs";
        validationResponse =
            StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
        log.info(comment);
      }
      if (validationResponse.getIsValid()) {
        String comment = "Hurray";
        validationResponse.setComment(comment);
        log.info(comment);
      }
      return validationResponse;
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new StitchMateGenericException("Node validation failed");
    }
  }

  public static <T extends BaseNodeRequest> ValidationResponse validateQueryParams(T queryParams) {
    ValidationResponse validationResponse = new ValidationResponse(true);
    try {
      Pattern p = Pattern.compile("\\{(.*?)\\}");
      Matcher m = p.matcher(queryParams.getQuery());
      List paramNames = new ArrayList<>();
      while (m.find()) {
        paramNames.add(m.group(1));
      }
      for (Parameter par : queryParams.getParameterConfig().getParametersList()) {
        if (!paramNames.contains(par.getParameterName())) {
          String comment = "Parameters match failed with query";
          validationResponse =
              StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
          log.info(comment);
        }
      }
      if (validationResponse.getIsValid()) {
        String comment = "Valid Query";
        validationResponse.setComment(comment);
        log.info(comment);
      }
      return validationResponse;
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new StitchMateGenericException("Query validation failed for node");
    }
  }

  public Boolean checkIfJobNameExists(String jobName) {
    JobMaster jobMaster = jobMasterRepository.getByJobName(jobName);
    return !Objects.isNull(jobMaster);
  }

  public boolean checkIfJobIdExists(Long jobId) {
    return Objects.nonNull(jobMasterRepository.getByJobId(jobId));
  }

  public boolean checkIfTagExists(String tag) {
    TagMaster tagMaster = tagMasterRepository.findTagMasterByTag(tag);
    return !Objects.isNull(tagMaster);
  }

  public boolean checkIfTagExists(List<Long> tagIds) {
    for (Long tagId : tagIds) {
      if (!tagMasterRepository.findById(tagId).isPresent()) {
        return false;
      }
    }
    return true;
  }

  public boolean checkIfTagJobMappingExists(long jobId, long tagId) {
    JobsTagsRelations jobsTagsRelations =
        jobsTagsRelationsRepository.findByJobIdAndTagId(jobId, tagId);
    return !Objects.isNull(jobsTagsRelations);
  }

  private boolean containsCycle(CreateEdgeRequest createEdgeRequest) {
    // fetch list of edges
    List<EdgeList> edgeList = edgeMasterRepository.getEdgeListByJobId(createEdgeRequest.getJobId());
    return CyclicUtils.containsCycle(
        edgeList, createEdgeRequest.getToNode(), createEdgeRequest.getFromNode());
  }

  public ValidationResponse checkIfJobExistsByIdAndName(JobActionRequest request) {
    ValidationResponse validationResponse = new ValidationResponse(true);
    JobMaster jobMaster =
        jobMasterRepository.findJobMasterByJobIdAAndJobNameWithActiveStatus(
            request.getJobId(), request.getJobName());
    if (jobMaster == null) {
      String comment = "Invalid job details";
      validationResponse =
          StitchUtils.updateValidationResponseOnFailure(validationResponse, comment);
      log.info(comment);
    }
    return validationResponse;
  }

  public boolean isFutureDate(LocalDateTime localDateTime) {
    return localDateTime.isAfter(LocalDateTime.now());
  }

  public boolean checkIdConfigKeyValuePairExists(AddConfigRequest configRequest) {
    List<ConfigMaster> configMasters =
        configTableRepository.findByConfigKeyAndConfigValue(
            configRequest.getConfigKey(), configRequest.getConfigValue());
    return configMasters.size() == 0;
  }

  public <T extends BaseNodeRequest> boolean checkIfDestinationTableExist(
      T request, String tableName) {
    DestinationTable destinationTable;
    if (request.getClass() == CreateNodeRequest.class) {
      destinationTable = nodeMasterRepository.getDestinationTableName(tableName);
    } else {
      EditNodeRequest editNodeRequest = (EditNodeRequest) request;
      destinationTable =
          nodeMasterRepository.getNotDestinationTableNodeMaster(
              tableName, editNodeRequest.getNodeId());
    }
    return Objects.nonNull(destinationTable);
  }

  public <T extends BaseNodeRequest> Boolean checkifNodeExists(T request) {
    NodeMaster nodeAndJobRelation;
    if (request.getClass() == CreateNodeRequest.class) {
      CreateNodeRequest createNodeRequest = (CreateNodeRequest) request;
      nodeAndJobRelation =
          nodeMasterRepository.findByNodeNameAndJobId(
              createNodeRequest.getNodeName(), createNodeRequest.getJobId());
    } else {
      EditNodeRequest editNodeRequest = (EditNodeRequest) request;
      nodeAndJobRelation =
          nodeMasterRepository.getNotNodeNameNodeMaster(
              editNodeRequest.getNodeName(),
              editNodeRequest.getJobId(),
              editNodeRequest.getNodeId());
    }
    return Objects.nonNull(nodeAndJobRelation);
  }

  public boolean checkIfJobIdExistsWithValidStatus(Long jobId) {
    return Objects.nonNull(jobMasterRepository.findJobMasterByJobIdWithValidStatus(jobId));
  }

  public boolean checkIfJobIdIsExpired(Long jobId) {
    return Objects.isNull(
        jobMasterRepository.findByJobIdAndJobStatusIsNotAndIsDeletedIsFalse(
            jobId, JobStatus.EXPIRED));
  }

  // It checks even if the job is soft deleted
  public boolean checkIfJobIdPresent(Long jobId) {
    return Objects.nonNull(jobMasterRepository.findByJobId(jobId));
  }
}
