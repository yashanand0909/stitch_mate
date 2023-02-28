package com.mate.services;

import com.mate.exceptions.StitchMateBadRequestException;
import com.mate.models.entities.TagMaster;
import com.mate.models.requests.tag.CreateTagRequest;
import com.mate.repositories.TagMasterRepository;
import com.mate.repositories.projections.TagList;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TagService {

  final TagMasterRepository tagMasterRepository;
  final ValidationService validationService;

  public TagService(TagMasterRepository tagMasterRepository, ValidationService validationService) {
    this.tagMasterRepository = tagMasterRepository;
    this.validationService = validationService;
  }

  public TagMaster createTag(CreateTagRequest createTagRequest, String createdBy) {
    TagMaster tagMaster = new TagMaster();
    if (!validationService.checkIfTagExists(createTagRequest.getTag())) {
      tagMaster.setTag(createTagRequest.getTag());
      tagMaster.setCreatedBy(createdBy);
      log.info("created tag: {}", tagMaster);
      return tagMasterRepository.save(tagMaster);
    } else {
      log.error("Failed to create as it already exists");
      throw new StitchMateBadRequestException(
          String.format("Tag: %s already exists", createTagRequest.getTag()));
    }
  }

  public Stream<TagList> getTags() {
    log.info("getting tags");
    return tagMasterRepository.getTagList().stream();
  }
}
