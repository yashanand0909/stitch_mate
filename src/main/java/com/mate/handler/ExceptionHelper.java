package com.mate.handler;

import com.mate.exceptions.AuthException;
import com.mate.exceptions.InvalidRequestException;
import com.mate.exceptions.StitchMateBadRequestException;
import com.mate.exceptions.StitchMateGenericException;
import com.mate.models.responses.BaseApiResponse;
import com.mate.models.responses.ErrorResponse;
import com.mate.utilities.StitchUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(annotations = RestController.class)
@Slf4j
public class ExceptionHelper extends ResponseEntityExceptionHandler {

  @ExceptionHandler(StitchMateGenericException.class)
  public ResponseEntity<? extends BaseApiResponse> handleStitchGenericException(
      StitchMateGenericException ex) {
    BaseApiResponse responseBody =
        StitchUtils.updateExceptionResponse(new BaseApiResponse(), ex.getMessage());
    return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<? extends BaseApiResponse> handleInvalidRequestException(
      InvalidRequestException ex) {
    BaseApiResponse responseBody =
        StitchUtils.updateExceptionResponse(new BaseApiResponse(), ex.getMessage());
    return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleInvalidInputException(Exception ex) {
    BaseApiResponse responseBody =
        StitchUtils.updateExceptionResponse(new BaseApiResponse(), ex.getMessage());
    return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<?> handleInvalidAuth(AuthException ex) {
    BaseApiResponse responseBody =
        StitchUtils.updateExceptionResponse(
            new BaseApiResponse(), "User Authentication Failed : " + ex.getMessage());
    return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    List<String> errorList = new ArrayList<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errorList.add("field " + error.getField() + " " + error.getDefaultMessage());
    }
    log.warn(
        "Validation errors : {} , Parameters : {}", errorList, ex.getBindingResult().getTarget());

    ErrorResponse error =
        new ErrorResponse("Validation Failed", errorList, HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(StitchMateBadRequestException.class)
  public ResponseEntity<? extends ErrorResponse> handleDataStitchBadRequestException(
      StitchMateGenericException ex) {
    List<String> errorList = new ArrayList<>();
    errorList.add(ex.getMessage());
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.BAD_REQUEST.getReasonPhrase(), errorList, HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }
}
