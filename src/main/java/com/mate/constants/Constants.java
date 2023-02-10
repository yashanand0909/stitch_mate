package com.mate.constants;

public class Constants {
  // Add constants in classes under constant package
  public static final String dagRunsEndPointOrderBy = "/dagRuns?order_by=-execution_date";
  public static final String dagEndpoint = "dags/";
  public static final String authorizationKey = "Authorization";
  public static final String cookieKey = "Cookie";
  public static final String basicKey = "Basic";
  public static final String sessionKey = "session";
  public static final String dagRunsEndpoint = "dagRuns/";
  public static final String taskInstancesEndPoint = "taskInstances/";
  public static final String logEndpoint = "logs/";
  public static final String contentType = "application/json";
  public static final String contentTypeKey = "Content-Type";
  public static final String slackMessageTemplate =
      "Current run status changed to *%s* for job name - *%s*";
  public static final String channelMessageTemplate = "<!channel>";

  public static final String msdUserEmailHeader = "msd-user-email";
  public static final String onceCron = "@once";
}
