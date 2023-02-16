package com.mate.models.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

  @Override
  public String convertToDatabaseColumn(Map<String, Object> stringObjectMap) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(stringObjectMap);
    } catch (JsonProcessingException e) {
      log.error("Could not convert map to json string.", e);
      return null;
    }
  }

  @Override
  public Map<String, Object> convertToEntityAttribute(String attribute) {
    if (attribute == null) {
      return new HashMap<>();
    }
    try {
      ObjectMapper objectMapper =
          JsonMapper.builder()
              .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
              .build();
      return objectMapper.readValue(attribute, HashMap.class);
    } catch (IOException e) {
      log.error("error while trying to convert string to map", e);
    }
    return new HashMap<>();
  }
}
