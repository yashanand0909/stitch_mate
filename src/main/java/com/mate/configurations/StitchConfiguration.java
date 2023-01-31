package com.mate.configurations;

import java.util.HashSet;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StitchConfiguration {

  @Bean
  public Set<String> createdBySet() {
    return new HashSet<>();
  }
}
