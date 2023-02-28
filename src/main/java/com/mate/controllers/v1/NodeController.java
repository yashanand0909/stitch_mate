package com.mate.controllers.v1;

import static com.mate.constants.Constants.msdUserEmailHeader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mate.exceptions.InvalidRequestException;
import com.mate.models.entities.NodeMaster;
import com.mate.models.requests.node.CreateNodeRequest;
import com.mate.models.requests.node.DeleteNodeRequest;
import com.mate.models.requests.node.EditNodeRequest;
import com.mate.models.requests.query.QueryValidatorRequest;
import com.mate.models.responses.node.DeleteNodeResponse;
import com.mate.models.responses.node.NodeResponse;
import com.mate.models.responses.node.NodeRunLogsResponse;
import com.mate.models.responses.query.QueryValidatorResponse;
import com.mate.models.responses.validation.ValidationResponse;
import com.mate.repositories.JobMasterRepository;
import com.mate.services.NodeService;
import com.mate.services.ValidationService;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Configuration
@RequestMapping("/v1/node")
public class NodeController {

  final JobMasterRepository jobMasterRepository;
  final ValidationService validationService;
  final NodeService nodeService;

  public NodeController(
      JobMasterRepository jobMasterRepository,
      ValidationService validationService,
      NodeService nodeService) {
    this.jobMasterRepository = jobMasterRepository;
    this.validationService = validationService;
    this.nodeService = nodeService;
  }

  @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<NodeResponse> createNode(
      @Valid @RequestBody CreateNodeRequest createNodeRequest,
      @NotNull @NotBlank @NotEmpty @RequestHeader(msdUserEmailHeader) String createdBy)
      throws JsonProcessingException {
    NodeResponse nodeResponse = new NodeResponse();
    ValidationResponse madatoryFieldValidation =
        validationService.checkMandatoryFieldsForCreateRequest(createNodeRequest);
    if (!madatoryFieldValidation.getIsValid())
      throw new InvalidRequestException(madatoryFieldValidation.getComment());
    ValidationResponse validationResponse =
        validationService.validateNodeRequest(createNodeRequest);
    if (!validationResponse.getIsValid())
      throw new InvalidRequestException(validationResponse.getComment());
    NodeMaster createdNode = nodeService.createNode(createNodeRequest, createdBy);
    if (createdNode != null) {
      nodeResponse.setNodeMaster(createdNode);
      nodeResponse.setMessage("Node created");
      return new ResponseEntity<>(nodeResponse, HttpStatus.OK);
    } else {
      nodeResponse.setStatus("FAILED");
      nodeResponse.setMessage("Node creation failed!");
      return new ResponseEntity<>(nodeResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(method = RequestMethod.DELETE, consumes = "application/json")
  public ResponseEntity<DeleteNodeResponse> deleteNode(
      @RequestBody DeleteNodeRequest deleteNodeRequest,
      @NotNull @NotBlank @NotEmpty @RequestHeader(msdUserEmailHeader) String deletedBy) {
    DeleteNodeResponse deleteNodeResponse = new DeleteNodeResponse();
    ValidationResponse validationResponse =
        validationService.validateNodeDeletionRequest(deleteNodeRequest);
    if (!validationResponse.getIsValid())
      throw new InvalidRequestException(validationResponse.getComment());
    NodeMaster nodeMaster = nodeService.deleteNode(deleteNodeRequest, deletedBy);
    if (nodeMaster != null && nodeMaster.isDeleted()) {
      deleteNodeResponse.setMessage("Node Deleted");
      deleteNodeResponse.setDeletedNodeId(nodeMaster.getNodeId());
      return new ResponseEntity<>(deleteNodeResponse, HttpStatus.OK);
    } else {
      deleteNodeResponse.setStatus("FAILED");
      deleteNodeResponse.setMessage("Node Deletion Failed !!");
      deleteNodeResponse.setDeletedNodeId(deleteNodeResponse.getDeletedNodeId());
      return new ResponseEntity<>(deleteNodeResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(method = RequestMethod.PATCH, consumes = "application/json")
  public ResponseEntity<NodeResponse> editNode(
      @Valid @RequestBody EditNodeRequest editNodeRequest,
      @NotNull @NotBlank @NotEmpty @RequestHeader(msdUserEmailHeader) String editedBy)
      throws JsonProcessingException {
    NodeResponse editNodeResponse = new NodeResponse();
    ValidationResponse madatoryFieldValidation =
        validationService.checkMandatoryFieldsForUpdateRequest(editNodeRequest);
    if (!madatoryFieldValidation.getIsValid())
      throw new InvalidRequestException(madatoryFieldValidation.getComment());
    ValidationResponse validationResponse = validationService.validateNodeRequest(editNodeRequest);
    if (!validationResponse.getIsValid())
      throw new InvalidRequestException(validationResponse.getComment());
    NodeMaster editNode = nodeService.editNode(editNodeRequest, editedBy);
    if (editNode != null) {
      editNodeResponse.setNodeMaster(editNode);
      editNodeResponse.setMessage("Node edited");
      return new ResponseEntity<>(editNodeResponse, HttpStatus.OK);
    } else {
      editNodeResponse.setStatus("FAILED");
      editNodeResponse.setMessage("Node editing failed!");
      return new ResponseEntity<>(editNodeResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(
      value = "/{nodeId}/details",
      method = RequestMethod.GET,
      produces = "application/json")
  public ResponseEntity<NodeResponse> getNodeDetails(@PathVariable("nodeId") Long nodeId) {
    NodeResponse nodeResponse = new NodeResponse();
    nodeResponse.setNodeMaster(nodeService.getNodeDetails(nodeId));
    nodeResponse.setMessage("Node details");
    return new ResponseEntity<>(nodeResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "/validate", method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<QueryValidatorResponse> validateQuery(
      @Valid @RequestBody QueryValidatorRequest queryValidatorRequest) {
    QueryValidatorResponse queryValidatorResponse = nodeService.queryParser(queryValidatorRequest);
    if (!queryValidatorResponse.getValid()) {
      queryValidatorResponse.setStatus("Failed");
    }
    return new ResponseEntity<>(queryValidatorResponse, HttpStatus.OK);
  }

  @RequestMapping(
      value = "/{jobName}/node/{nodeId}/logs",
      method = RequestMethod.GET,
      produces = "application/json")
  public ResponseEntity<NodeRunLogsResponse> getNodeRunLogs(
      @PathVariable(value = "jobName") @NotNull String jobName,
      @RequestParam(name = "jobRunId") @NotNull String jobRunId,
      @PathVariable(value = "nodeId") @NotNull Long nodeId) {
    NodeRunLogsResponse nodeRunLogsResponse = new NodeRunLogsResponse();
    nodeRunLogsResponse.setData(nodeService.getNodeRunLogs(jobName, jobRunId, nodeId));
    nodeRunLogsResponse.setMessage("Node Runs Logs");
    return new ResponseEntity<>(nodeRunLogsResponse, HttpStatus.OK);
  }
}
