package com.mate.controllers.v1;

import static com.mate.constants.Constants.msdUserEmailHeader;

import com.mate.models.entities.TagMaster;
import com.mate.models.requests.tag.CreateTagRequest;
import com.mate.models.responses.tag.CreateTagResponse;
import com.mate.models.responses.tag.GetTagListResponse;
import com.mate.repositories.projections.TagList;
import com.mate.services.TagService;
import java.util.stream.Stream;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(path = "/v1/tag")
public class TagController {

  final TagService tagService;

  public TagController(TagService tagService) {
    this.tagService = tagService;
  }

  @PostMapping(path = "/", produces = "application/json")
  public ResponseEntity<CreateTagResponse> createTag(
      @Valid @RequestBody CreateTagRequest createTagRequest,
      @NotNull @NotBlank @NotEmpty @RequestHeader(msdUserEmailHeader) String createdBy) {
    CreateTagResponse createTagResponse = new CreateTagResponse();
    TagMaster tagMaster = tagService.createTag(createTagRequest, createdBy);
    createTagResponse.setData(tagMaster);
    createTagResponse.setMessage("Tag Created");
    return new ResponseEntity<>(createTagResponse, HttpStatus.OK);
  }

  @GetMapping("/")
  public ResponseEntity<GetTagListResponse> getTags() {
    GetTagListResponse getTagListResponse = new GetTagListResponse();
    Stream<TagList> tagLists = tagService.getTags();
    getTagListResponse.setData(tagLists);
    getTagListResponse.setMessage("Tag List");
    return new ResponseEntity<>(getTagListResponse, HttpStatus.OK);
  }
}
