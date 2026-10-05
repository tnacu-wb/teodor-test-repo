package uk.co.whitbread.dashboard.infrastructure.rest.client.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DashboardClientExceptionHandler {

  @ResponseBody
  @ExceptionHandler(value = DashboardClientException.class)
  public ResponseEntity<DashboardClientError> handleException(DashboardClientException exception) {

    final DashboardClientError errorResponse = new DashboardClientError(exception.getStatus(), exception.getErrorCode(),
        new String[]{exception.getMessage()});

    return ResponseEntity.status(exception.getStatus()).body(errorResponse);

  }
}
