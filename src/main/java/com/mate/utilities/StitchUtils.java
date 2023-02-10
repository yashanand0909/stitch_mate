package com.mate.utilities;

import com.mate.models.responses.BaseApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StitchUtils {

  public static <T extends BaseApiResponse> T updateExceptionResponse(
      T responseBody, String message) {
    responseBody.setStatus("FAILED");
    responseBody.setMessage(message);
    return responseBody;
  }
}
