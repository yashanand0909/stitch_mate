package com.mate.services;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;

import com.mate.exceptions.StitchMateBadRequestException;
import com.mate.models.entities.TagMaster;
import com.mate.models.requests.tag.CreateTagRequest;
import com.mate.repositories.TagMasterRepository;
import com.mate.repositories.projections.TagList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestTagService {

  @Mock TagMasterRepository tagMasterRepository;

  @InjectMocks TagService tagService;

  @Mock ValidationService validationService;

  @Test
  @SneakyThrows
  public void testCreateTag() {
    TagMaster tagMaster = new TagMaster();
    CreateTagRequest createTagRequest = new CreateTagRequest();
    createTagRequest.setTag("test-tag");
    tagMaster.setTag("test-tag");
    tagMaster.setCreatedBy("TEST");
    when(tagMasterRepository.save(any(TagMaster.class))).thenReturn(tagMaster);
    when(validationService.checkIfTagExists(anyString())).thenReturn(false);
    assert tagMaster.equals(tagService.createTag(createTagRequest, "TEST"));
  }

  @Test
  public void testCreateTagError() {
    TagMaster tagMaster = new TagMaster();
    CreateTagRequest createTagRequest = new CreateTagRequest();
    createTagRequest.setTag("test-tag");
    tagMaster.setTag("test-tag");
    tagMaster.setCreatedBy("TEST");
    when(validationService.checkIfTagExists(anyString())).thenReturn(true);
    assertThrows(
        StitchMateBadRequestException.class, () -> tagService.createTag(createTagRequest, "TEST"));
  }

  @Test
  public void testGetTags() {
    TagList tagList =
        new TagList() {
          @Override
          public String getTag() {
            return "new-tag";
          }

          @Override
          public long getTagId() {
            return 1;
          }
        };
    ArrayList<TagList> tags = new ArrayList<>();
    TagList tagList1 =
        new TagList() {
          @Override
          public String getTag() {
            return "new-tag";
          }

          @Override
          public long getTagId() {
            return 1;
          }
        };
    tags.add(tagList1);
    Stream<TagList> tagsStream = tags.stream();
    List<TagList> tagListList = new ArrayList<>();
    tagListList.add(tagList);
    when(tagMasterRepository.getTagList()).thenReturn(tagListList);
    Stream<TagList> tlist = tagService.getTags();
    Iterator<?> i1 = tagsStream.iterator();
    Iterator<?> i2 = tlist.iterator();

    while (i1.hasNext() && i2.hasNext()) {
      TagList t1 = (TagList) i1.next();
      TagList t2 = (TagList) i2.next();
      assertEquals(t1.getTag(), t2.getTag());
    }
    assert !i1.hasNext() && !i2.hasNext();
  }
}
