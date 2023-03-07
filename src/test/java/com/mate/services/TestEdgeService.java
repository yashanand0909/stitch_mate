package com.mate.services;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mate.exceptions.StitchMateGenericException;
import com.mate.models.entities.EdgeMaster;
import com.mate.models.entities.JobMaster;
import com.mate.models.requests.edge.CreateEdgeRequest;
import com.mate.models.requests.edge.DeleteEdgeRequest;
import com.mate.repositories.EdgeMasterRepository;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestEdgeService {

  @Mock EdgeMasterRepository edgeMasterRepository;
  @InjectMocks EdgeService edgeService;

  EdgeMaster edgeMaster = new EdgeMaster();
  JobMaster jobMaster;
  EdgeMaster edgeMasterDeleted = new EdgeMaster();
  CreateEdgeRequest createEdgeRequest;
  DeleteEdgeRequest deleteEdgeRequest;

  @Before
  public void setup() {
    edgeMaster.setJobId(1L);
    edgeMaster.setFromNode(34L);
    edgeMaster.setToNode(45L);
    edgeMaster.setCreatedBy("TEST");
    edgeMasterDeleted.setDeleted(true);
    edgeMasterDeleted.setJobId(1L);
    edgeMasterDeleted.setFromNode(34L);
    edgeMasterDeleted.setToNode(45L);
    edgeMasterDeleted.setCreatedBy("TEST");
    edgeMasterDeleted.setDeletedBy("TEST");

    createEdgeRequest = new CreateEdgeRequest(123L, 345L);
    createEdgeRequest.setJobId(1L);
    deleteEdgeRequest = new DeleteEdgeRequest(123L);
    deleteEdgeRequest.setJobId(1L);
  }

  @Test
  @SneakyThrows
  public void testCreateEdge() {
    when(edgeMasterRepository.save(any(EdgeMaster.class))).thenReturn(edgeMaster);
    assertEquals(edgeMaster, edgeService.createEdge(createEdgeRequest, "TEST"));
  }

  @Test
  @SneakyThrows
  public void testCreateEdgeNullPointerException() {
    when(edgeMasterRepository.save(any(EdgeMaster.class))).thenThrow(new NullPointerException());
    assertThrows(
        StitchMateGenericException.class, () -> edgeService.createEdge(createEdgeRequest, "TEST"));
  }

  @Test
  @SneakyThrows
  public void testDeleteEdge() {
    when(edgeMasterRepository.findByEdgeId(any(Long.class))).thenReturn(edgeMaster);
    when(edgeMasterRepository.save(any(EdgeMaster.class))).thenReturn(edgeMasterDeleted);
    assertEquals(edgeMasterDeleted, edgeService.deleteEdge(deleteEdgeRequest, "TEST"));
  }

  @Test
  @SneakyThrows
  public void testDeleteEdgeThrowNullPointer() {
    when(edgeMasterRepository.findByEdgeId(any(Long.class))).thenReturn(edgeMaster);
    when(edgeMasterRepository.save(any(EdgeMaster.class))).thenThrow(new NullPointerException());
    assertThrows(
        StitchMateGenericException.class, () -> edgeService.deleteEdge(deleteEdgeRequest, "TEST"));
  }

  @Test
  @SneakyThrows
  public void testDeleteEdgeEdgeMasterNullOperation() {
    when(edgeMasterRepository.findByEdgeId(any(Long.class))).thenReturn(null);
    assertNull(edgeService.deleteEdge(deleteEdgeRequest, "TEST"));
  }
}
