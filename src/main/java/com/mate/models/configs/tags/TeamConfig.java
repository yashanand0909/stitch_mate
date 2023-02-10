package com.mate.models.configs.tags;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamConfig {

  private String teamName;

  private String primaryPointOfContact;

  private String secondaryPointOfContact;

  private String email;
}
