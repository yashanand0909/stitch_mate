package com.mate.repositories;

import com.mate.models.entities.ConfigMaster;
import com.mate.repositories.projections.ConfigList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigTableRepository extends JpaRepository<ConfigMaster, Long> {

  ConfigMaster findById(long id);

  @Query(
      "select configTable.configValue as configValue from ConfigMaster configTable "
          + "where configTable.configKey = :configKey")
  List<ConfigList> findByConfigKey(String configKey);

  List<ConfigMaster> findByConfigKeyAndConfigValue(String configKey, String configValue);
}
