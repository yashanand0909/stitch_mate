package com.mate.controllers.v1;

import com.mate.models.requests.config.AddConfigRequest;
import com.mate.models.requests.config.UpdateConfigRequest;
import com.mate.models.responses.config.ConfigResponse;
import com.mate.models.responses.config.GetConfigValuesResponse;
import com.mate.services.ConfigService;
import com.mate.utilities.authUtils.JwtTokenUtil;
import javax.validation.Valid;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Configuration
@RequestMapping("/v1/config")
public class ConfigController {

  final ConfigService configService;
  final JwtTokenUtil jwtTokenUtil;

  public ConfigController(ConfigService configService, JwtTokenUtil jwtTokenUtil) {
    this.configService = configService;
    this.jwtTokenUtil = jwtTokenUtil;
  }

  @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<ConfigResponse> addConfig(
      @Valid @RequestBody AddConfigRequest addConfigRequest,
      @RequestHeader(name = "Authorization") String authorization) {
    ConfigResponse configResponse = new ConfigResponse();
    String user = jwtTokenUtil.getUsernameFromToken(authorization);
    addConfigRequest.setCreatedBy(user);
    configResponse.setConfig(configService.createConfig(addConfigRequest));
    return new ResponseEntity<>(configResponse, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PATCH, consumes = "application/json")
  public ResponseEntity<ConfigResponse> addConfig(
      @Valid @RequestBody UpdateConfigRequest updateConfigRequest,
      @RequestHeader(name = "Authorization") String authorization) {
    ConfigResponse configResponse = new ConfigResponse();
    String user = jwtTokenUtil.getUsernameFromToken(authorization);
    updateConfigRequest.setUpdatedBy(user);
    configResponse.setConfig(configService.updateConfig(updateConfigRequest));
    return new ResponseEntity<>(configResponse, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, produces = "application/json")
  public ResponseEntity<GetConfigValuesResponse> addConfig(
      @RequestParam(name = "config_key") String configKey) {
    GetConfigValuesResponse getConfigValuesResponse = new GetConfigValuesResponse();
    getConfigValuesResponse.setValuesList(configService.getConfigValues(configKey));
    return new ResponseEntity<>(getConfigValuesResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "createdBy", method = RequestMethod.GET, produces = "application/json")
  public ResponseEntity<GetConfigValuesResponse> getCreatedByConfig() {
    GetConfigValuesResponse getConfigValuesResponse = new GetConfigValuesResponse();
    getConfigValuesResponse.setValuesList(configService.getCreatedByList());
    return new ResponseEntity<>(getConfigValuesResponse, HttpStatus.OK);
  }
}
