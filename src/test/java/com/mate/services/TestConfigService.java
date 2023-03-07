package com.mate.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.mate.exceptions.InvalidRequestException;
import com.mate.models.entities.ConfigMaster;
import com.mate.models.requests.config.AddConfigRequest;
import com.mate.models.requests.config.UpdateConfigRequest;
import com.mate.repositories.ConfigTableRepository;
import com.mate.repositories.projections.ConfigList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestConfigService {

  @Mock ConfigTableRepository configTableRepository;

  @Mock ValidationService validationService;

  @InjectMocks ConfigService configService;

  ConfigMaster configMaster = new ConfigMaster();
  AddConfigRequest addConfigRequest = new AddConfigRequest();
  UpdateConfigRequest updateConfigRequest = new UpdateConfigRequest();
  ConfigList configList =
      new ConfigList() {
        @Override
        public String getConfigValue() {
          return "testValue";
        }
      };

  @Test
  @SneakyThrows
  public void testCreateConfig() {
    when(validationService.checkIdConfigKeyValuePairExists(any(AddConfigRequest.class)))
        .thenReturn(true);
    when(configTableRepository.save(any(ConfigMaster.class))).thenReturn(configMaster);
    assertEquals(configMaster, configService.createConfig(addConfigRequest));
    when(validationService.checkIdConfigKeyValuePairExists(any(AddConfigRequest.class)))
        .thenReturn(false);
    assertThrows(InvalidRequestException.class, () -> configService.createConfig(addConfigRequest));
  }

  @Test
  @SneakyThrows
  public void testUpdateConfig() {
    when(configTableRepository.findById(any())).thenReturn(Optional.of(configMaster));
    when(configTableRepository.save(any(ConfigMaster.class))).thenReturn(configMaster);
    assertEquals(configMaster, configService.updateConfig(updateConfigRequest));
    when(configTableRepository.findById(any())).thenReturn(Optional.empty());
    assertThrows(
        InvalidRequestException.class, () -> configService.updateConfig(updateConfigRequest));
  }

  @Test
  @SneakyThrows
  public void testGetConfig() {
    List<ConfigList> mockList = Collections.singletonList(configList);
    when(configTableRepository.findByConfigKey(anyString())).thenReturn(mockList);
    List<String> result = configService.getConfigValues("Test");
    assertEquals(1, result.size());
    assertEquals("testValue", result.get(0));
  }
}
