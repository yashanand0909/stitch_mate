package com.mate.utilities;

import static com.mate.constants.Constants.contentType;
import static com.mate.constants.Constants.contentTypeKey;

import com.mate.constants.Constants;
import com.mate.exceptions.StitchMateGenericException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpUtils {

  private static Map<String, String> headersMap =
      Stream.of(
              new String[][] {
                {"accept", "application/json"},
                {"Content-Type", "application/json"},
                {"Authorization", "Basic YWRtaW46YWRtaW5AMTIz"},
                {
                  "Cookie",
                  "session=d5eb3c57-6c02-4dde-92c0-861a3227268f.L9kkzlgne_GfQ7icVSHHGzYuJR8"
                },
              })
          .collect(Collectors.toMap(data -> data[0], data -> data[1]));

  public static boolean patchRequest(Object body, String url) {
    try {
      log.info("sending patch request");
      HttpResponse<String> response = Unirest.patch(url).headers(headersMap).body(body).asString();
      return response.getStatus() == 200 || response.getStatus() == 201;
    } catch (Exception e) {
      log.error(e.getMessage());
      return false;
    }
  }

  public static boolean putRequest(Object body, String url) {
    try {
      log.info("sending put request");
      HttpResponse<String> response = Unirest.put(url).headers(headersMap).body(body).asString();
      return response.getStatus() == 200 || response.getStatus() == 201;
    } catch (UnirestException e) {
      log.error(e.getMessage());
      return false;
    }
  }

  public static String get(String url, String auth, String session) throws UnirestException {
    try {
      headersMap.put(Constants.authorizationKey, Constants.basicKey + " " + auth);
      headersMap.put(Constants.cookieKey, Constants.sessionKey + "=" + session);
      log.info("sending get request");
      HttpResponse<String> response =
          Unirest.get(url)
              .headers(headersMap)
              .socketTimeout(60000)
              .connectTimeout(10000)
              .asString();
      if (response.getStatus() == 200 || response.getStatus() == 201) {
        return response.getBody();
      } else
        throw new StitchMateGenericException("Downstream call failed. Please try after sometime");
    } catch (UnirestException e) {
      log.error(e.getMessage());
      throw new StitchMateGenericException("Downstream call failed. Please try after sometime");
    }
  }

  public static String postRequest(String body, String url) {
    try {
      log.info("sending post request");
      HttpResponse<String> response =
          Unirest.post(url).header(contentTypeKey, contentType).body(body).asString();

      if (response.getStatus() == 200 || response.getStatus() == 201) {
        return response.getBody();
      } else {
        throw new StitchMateGenericException(
            "Something went wrong when making post request to : " + response.getBody());
      }
    } catch (UnirestException e) {
      log.error(e.getMessage());
      throw new StitchMateGenericException("Something went wrong when making post request to");
    }
  }
}
