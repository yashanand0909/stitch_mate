package com.mate.services;

import com.mate.exceptions.StitchMateGenericException;
import com.mate.models.entities.EdgeMaster;
import com.mate.models.requests.edge.CreateEdgeRequest;
import com.mate.models.requests.edge.DeleteEdgeRequest;
import com.mate.repositories.EdgeMasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EdgeService {

  final EdgeMasterRepository edgeMasterRepository;

  final ValidationService validationService;

  public EdgeService(
      EdgeMasterRepository edgeMasterRepository, ValidationService validationService) {
    this.edgeMasterRepository = edgeMasterRepository;
    this.validationService = validationService;
  }

  public EdgeMaster createEdge(CreateEdgeRequest createEdgeRequest, String createdBy) {
    EdgeMaster edgeMaster = new EdgeMaster();
    BeanUtils.copyProperties(createEdgeRequest, edgeMaster);
    edgeMaster.setCreatedBy(createdBy);
    try {
      edgeMaster = edgeMasterRepository.save(edgeMaster);
      log.info("create edge: {}", edgeMaster);
      return edgeMaster;
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new StitchMateGenericException(
          "Internal error occurred while creating the edge with message - " + e.getMessage());
    }
  }

  public EdgeMaster deleteEdge(DeleteEdgeRequest deleteEdgeRequest, String deletedBy) {
    try {
      EdgeMaster edgeMaster = edgeMasterRepository.findByEdgeId(deleteEdgeRequest.getEdgeId());
      if (edgeMaster != null) {
        edgeMaster.setDeleted(true);
        edgeMaster.setDeletedBy(deletedBy);
        edgeMaster = edgeMasterRepository.save(edgeMaster);
      }
      log.info("delete edge: {}", edgeMaster);
      return edgeMaster;
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new StitchMateGenericException(
          "Internal error occurred while deleting the edge with message - " + e.getMessage());
    }
  }
}
