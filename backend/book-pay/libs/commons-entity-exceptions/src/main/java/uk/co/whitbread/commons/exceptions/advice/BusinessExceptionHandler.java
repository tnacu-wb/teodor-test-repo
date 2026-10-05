package uk.co.whitbread.commons.exceptions.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBusinessValidationException;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BusinessExceptionHandler {

  @ExceptionHandler(AbstractInternalException.class)
  public ResponseEntity<ErrorResponse> handleInternalServerException(
      AbstractInternalException e) {
    var response = ErrorResponse.builder()
        .errCode(e.getErrorCode())
        .debugMessage(e.getDebugMessage())
        .globalErrTextTemplate(e.getGlobalErrTextTemplate())
        .build();
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(AbstractBadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequestException(
      AbstractBadRequestException e) {
    var response = ErrorResponse.builder()
        .errCode(e.getErrorCode())
        .debugMessage(e.getDebugMessage())
        .globalErrTextTemplate(e.getGlobalErrTextTemplate())
        .build();
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AbstractNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(AbstractNotFoundException e) {
    var response = ErrorResponse.builder()
        .errCode(e.getErrorCode())
        .debugMessage(e.getDebugMessage())
        .globalErrTextTemplate(e.getGlobalErrTextTemplate())
        .build();
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(AbstractBusinessValidationException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      AbstractBusinessValidationException e) {
    var response = ErrorResponse.builder()
        .errCode(e.getErrorCode())
        .debugMessage(e.getDebugMessage())
        .globalErrTextTemplate(e.getGlobalErrTextTemplate())
        .build();
    if (e.getValidationErrors() != null && !e.getValidationErrors().isEmpty()) {
      e.getValidationErrors().forEach(response::addValidationError);
    }
    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

}