package com.mate.controllers.v1;

import static com.mate.constants.Constants.msdUserEmailHeader;

import com.mate.models.requests.notification.NotificationRequest;
import com.mate.models.responses.notification.NotificationResponse;
import com.mate.services.NotificationService;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Configuration
@RequestMapping("/v1/notify")
public class NotificationController {

  final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @RequestMapping(value = "/slack", method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<NotificationResponse> slackNotify(
      @Valid @RequestBody NotificationRequest notificationRequest,
      @NotNull @NotBlank @NotEmpty @RequestHeader(msdUserEmailHeader) String actionBy) {

    NotificationResponse notificationResponse =
        notificationService.sendSlackAlertWithAttachment(
            notificationRequest.getJobId(), notificationRequest.getStatus(), actionBy);
    return new ResponseEntity<>(notificationResponse, HttpStatus.OK);
  }
}
