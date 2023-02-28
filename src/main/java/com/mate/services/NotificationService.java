package com.mate.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mate.exceptions.StitchMateGenericException;
import com.mate.models.configs.notification.NotificationConfig;
import com.mate.models.configs.notification.SlackConfigSchema;
import com.mate.models.entities.JobMaster;
import com.mate.models.enums.JobStatus;
import com.mate.models.requests.notification.SlackNotificationRequest;
import com.mate.models.responses.notification.NotificationResponse;
import com.mate.models.slack.Attachment;
import com.mate.models.slack.AttachmentBuilder;
import com.mate.models.slack.Block;
import com.mate.models.slack.BlockFields;
import com.mate.repositories.JobMasterRepository;
import com.mate.repositories.NodeRunsRepository;
import com.mate.utilities.SlackUtils;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  @Value("${default_slack_webhook}")
  private String slack_Webhook_Url;

  @Value("${default_slack_channel}")
  private String channel;

  JobMasterRepository jobMasterRepository;
  ValidationService validationService;
  ObjectMapper objectMapper;
  NodeRunsRepository nodeRunsRepository;

  public NotificationService(
      JobMasterRepository jobMasterRepository,
      ValidationService validationService,
      ObjectMapper objectMapper,
      NodeRunsRepository nodeRunsRepository) {
    this.jobMasterRepository = jobMasterRepository;
    this.validationService = validationService;
    this.objectMapper = objectMapper;
    this.nodeRunsRepository = nodeRunsRepository;
  }

  public NotificationResponse notifySLack(
      SlackNotificationRequest slackNotificationRequest, String text)
      throws JsonProcessingException {
    SlackUtils.sendSlackMessage(
        slackNotificationRequest.getChannelName(),
        slackNotificationRequest.getAttachment(),
        slack_Webhook_Url,
        text);
    NotificationResponse notificationResponse = new NotificationResponse();
    notificationResponse.setIsNotificationSent(true);
    return notificationResponse;
  }

  public NotificationResponse sendSlackAlertWithAttachment(
      Long jobId, String status, String actionBy) {
    try {
      if (validationService.checkIfJobIdPresent(jobId)) {
        JobMaster jobMaster = jobMasterRepository.findByJobId(jobId);
        if (Objects.nonNull(jobMaster.getNotificationConfig())) {
          SlackConfigSchema slackConfigSchema =
              (SlackConfigSchema)
                  objectMapper.readValue(
                      jobMaster.getNotificationConfig(), NotificationConfig.class);

          AttachmentBuilder attachmentBuilder = new AttachmentBuilder();

          switch (status) {
            case "FAILED":
              attachmentBuilder.setColor(Attachment.COLOR_DANGER);
              break;
            case "EXPIRED":
              attachmentBuilder.setColor(Attachment.COLOR_WARNING);
              break;
            case "PAUSED":
              attachmentBuilder.setColor(Attachment.COLOR_INFO);
              break;
            case "ACTIVE":
              attachmentBuilder.setColor(Attachment.COLOR_GOOD);
              break;
            default:
              attachmentBuilder.setColor(Attachment.COLOR_DANGER);
              break;
          }

          BlockFields blockFields = new BlockFields();
          blockFields.setType("plain_text");
          blockFields.setText(status);

          Block block = new Block();
          block.setType("header");
          block.setText(blockFields);

          BlockFields blockFields2 = new BlockFields();
          blockFields2.setType("mrkdwn");
          blockFields2.setText(String.format("*Job Name:* %n %s ", jobMaster.getJobName()));

          BlockFields blockFields3 = new BlockFields();
          blockFields3.setType("mrkdwn");
          blockFields3.setText(String.format("*Created By:* %n %s ", jobMaster.getCreatedBy()));

          Block block2 = new Block();
          block2.setType("section");
          block2.addField(blockFields2);
          block2.addField(blockFields3);

          BlockFields blockFields4 = new BlockFields();
          blockFields4.setType("mrkdwn");
          blockFields4.setText(
              "*Execution Summary:* "
                  + "Current Run Failed, Please check the logs in "
                  + "Datastitch last 10 Runs Directory to get more Information");

          Block block3 = new Block();
          block3.setType("section");
          block3.addField(blockFields4);

          BlockFields blockFields5 = new BlockFields();
          blockFields5.setType("mrkdwn");
          blockFields5.setText("*ActionBy:* " + actionBy);
          Block block4 = new Block();
          block4.setType("section");
          block4.addField(blockFields5);

          if (status.equals(JobStatus.FAILED.name())) {
            attachmentBuilder.addField(block);
            attachmentBuilder.addField(block2);
            attachmentBuilder.addField(block3);
          } else if (status.equals(JobStatus.ACTIVE.name())
              || status.equals(JobStatus.PAUSED.name())
              || status.equals("DELETED")) {

            attachmentBuilder.addField(block);
            attachmentBuilder.addField(block2);
            attachmentBuilder.addField(block4);
          } else {
            attachmentBuilder.addField(block);
            attachmentBuilder.addField(block2);
          }

          SlackNotificationRequest slackNotificationRequest =
              new SlackNotificationRequest(
                  slackConfigSchema.getChannelName() == null
                      ? channel
                      : slackConfigSchema.getChannelName(),
                  attachmentBuilder.build());
          if (status.equals(JobStatus.FAILED.name()) || status.equals(JobStatus.EXPIRED.name())) {
            return notifySLack(slackNotificationRequest, "<!channel>");
          } else {
            return notifySLack(slackNotificationRequest, "");
          }
        }
        throw new StitchMateGenericException(
            "Notification config is missing for job Id : " + jobId);
      }
      throw new StitchMateGenericException(String.format("Job with Id: %s does not exists", jobId));
    } catch (Exception e) {
      throw new StitchMateGenericException(e.getMessage());
    }
  }
}
