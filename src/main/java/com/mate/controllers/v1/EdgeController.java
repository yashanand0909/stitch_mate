package com.mate.controllers.v1;

import static com.mate.constants.Constants.msdUserEmailHeader;

import com.mate.exceptions.InvalidRequestException;
import com.mate.models.entities.EdgeMaster;
import com.mate.models.requests.edge.CreateEdgeRequest;
import com.mate.models.requests.edge.DeleteEdgeRequest;
import com.mate.models.responses.edge.CreateEdgeResponse;
import com.mate.models.responses.edge.DeleteEdgeResponse;
import com.mate.models.responses.validation.ValidationResponse;
import com.mate.services.EdgeService;
import com.mate.services.ValidationService;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Configuration
@RequestMapping("/v1/edge")
public class EdgeController {

  final EdgeService edgeService;
  final ValidationService validationService;

  @Autowired
  public EdgeController(EdgeService edgeService, ValidationService validationService) {
    this.edgeService = edgeService;
    this.validationService = validationService;
  }

  @PostMapping(consumes = "application/json")
  public ResponseEntity<CreateEdgeResponse> createEdge(
      @Valid @RequestBody CreateEdgeRequest createEdgeRequest,
      @NotNull @NotBlank @NotEmpty @RequestHeader(msdUserEmailHeader) String createdBy) {
    CreateEdgeResponse createEdgeResponse = new CreateEdgeResponse();
    ValidationResponse validationResponse =
        validationService.validateEdgeCreationRequest(createEdgeRequest);
    if (!validationResponse.getIsValid())
      throw new InvalidRequestException(validationResponse.getComment());
    EdgeMaster createdEdge = edgeService.createEdge(createEdgeRequest, createdBy);
    if (createdEdge != null) {
      createEdgeResponse.setEdgeMaster(createdEdge);
      createEdgeResponse.setMessage("Edge created");
      return new ResponseEntity<>(createEdgeResponse, HttpStatus.OK);
    } else {
      createEdgeResponse.setStatus("FAILED");
      createEdgeResponse.setMessage("Edge creation failed");
      return new ResponseEntity<>(createEdgeResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @RequestMapping(method = RequestMethod.DELETE, consumes = "application/json")
  public ResponseEntity<DeleteEdgeResponse> deleteEdge(
      @Valid @RequestBody DeleteEdgeRequest deleteEdgeRequest,
      @NotNull @NotBlank @NotEmpty @RequestHeader(msdUserEmailHeader) String deletedBy) {
    DeleteEdgeResponse deleteEdgeResponse = new DeleteEdgeResponse();
    ValidationResponse validationResponse =
        validationService.validateEdgeDeletionRequest(deleteEdgeRequest);
    if (!validationResponse.getIsValid())
      throw new InvalidRequestException(validationResponse.getComment());
    EdgeMaster edgeMaster = edgeService.deleteEdge(deleteEdgeRequest, deletedBy);
    if (edgeMaster != null && edgeMaster.isDeleted()) {
      deleteEdgeResponse.setMessage("Edge Deleted");
      deleteEdgeResponse.setDeletedEdgeId(edgeMaster.getEdgeId());
      return new ResponseEntity<>(deleteEdgeResponse, HttpStatus.OK);
    } else {
      deleteEdgeResponse.setStatus("FAILED");
      deleteEdgeResponse.setMessage("Edge Deletion Failed");
      deleteEdgeResponse.setDeletedEdgeId(deleteEdgeRequest.getEdgeId());
      return new ResponseEntity<>(deleteEdgeResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
