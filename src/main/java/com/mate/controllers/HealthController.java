package com.mate.controllers;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Configuration
public class HealthController {
  // Add controllers in classes under controller package
  @RequestMapping(
      value = {"/health_check", "/healthcheck"},
      method = RequestMethod.GET)
  public ResponseEntity<String> healthCheck() {
    ResponseEntity<String> response = null;

    response = new ResponseEntity<>("I am running FINE !!", HttpStatus.OK);

    return response;
  }
}
