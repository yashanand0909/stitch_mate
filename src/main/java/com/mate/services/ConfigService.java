package com.mate.services;

import com.mate.exceptions.InvalidRequestException;
import com.mate.models.entities.ConfigMaster;
import com.mate.models.requests.config.AddConfigRequest;
import com.mate.models.requests.config.UpdateConfigRequest;
import com.mate.repositories.ConfigTableRepository;
import com.mate.repositories.JobMasterRepository;
import com.mate.repositories.projections.ConfigList;
import com.mate.repositories.projections.CreatedByList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

  final ConfigTableRepository configTableRepository;
  final ValidationService validationService;
  final JobMasterRepository jobMasterRepository;
  final Set<String> createdBySet;

  public ConfigService(
      ConfigTableRepository configTableRepository,
      ValidationService validationService,
      JobMasterRepository jobMasterRepository,
      Set<String> createdBySet) {
    this.configTableRepository = configTableRepository;
    this.validationService = validationService;
    this.jobMasterRepository = jobMasterRepository;
    this.createdBySet = createdBySet;
  }

  public ConfigMaster createConfig(AddConfigRequest configRequest) {
    if (validationService.checkIdConfigKeyValuePairExists(configRequest)) {
      ConfigMaster configMaster = new ConfigMaster();
      BeanUtils.copyProperties(configRequest, configMaster);
      configMaster.setUpdatedBy(configRequest.getCreatedBy());
      return configTableRepository.save(configMaster);
    }
    throw new InvalidRequestException("Config key value pair already exists");
  }

  public ConfigMaster updateConfig(UpdateConfigRequest configRequest) {
    Optional<ConfigMaster> configTableOptional =
        configTableRepository.findById(configRequest.getConfigId());
    if (configTableOptional.isPresent()) {
      ConfigMaster configMaster = configTableOptional.get();
      if (Objects.nonNull(configRequest.getConfigKey()))
        configMaster.setConfigKey(configRequest.getConfigKey());
      configMaster.setConfigValue(configRequest.getConfigValue());
      configMaster.setUpdatedBy(configRequest.getUpdatedBy());
      return configTableRepository.save(configMaster);
    }
    throw new InvalidRequestException(
        "No config exists with given configId : " + configRequest.getConfigId());
  }

  public List<String> getConfigValues(String configKey) {
    List<String> configValues = new ArrayList<>();
    List<ConfigList> configLists = configTableRepository.findByConfigKey(configKey);
    if (configLists.size() == 0)
      throw new InvalidRequestException("No values exists for config key : " + configKey);
    for (ConfigList config : configLists) {
      configValues.add(config.getConfigValue());
    }
    return configValues;
  }

  public List<String> getCreatedByList() {
    if (createdBySet.size() == 0) {
      refreshCreatedBycache();
    }
    return new ArrayList<>(createdBySet);
  }

  public void updateCreatedByConfig(String createdBy) {
    if (createdBySet.size() == 0) {
      refreshCreatedBycache();
    } else createdBySet.add(createdBy);
  }

  private void refreshCreatedBycache() {
    List<CreatedByList> createdByList = jobMasterRepository.findAllCreatedBy();
    for (CreatedByList createdBy : createdByList) {
      createdBySet.add(createdBy.getCreatedBy());
    }
  }
}
