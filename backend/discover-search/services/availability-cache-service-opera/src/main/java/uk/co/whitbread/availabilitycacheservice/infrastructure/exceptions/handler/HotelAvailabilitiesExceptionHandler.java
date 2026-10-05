package uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.handler;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.HotelAvailabilitiesException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.HotelAvailabilitiesExceptionResponse;

@Slf4j
@ControllerAdvice
public class HotelAvailabilitiesExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(HotelAvailabilitiesException.class)
  public ResponseEntity<HotelAvailabilitiesExceptionResponse> handleHotelAvailabilitiesException(
      final HotelAvailabilitiesException ex,
      final HttpServletRequest request) {
    log.error("HotelAvailabilitiesException: ", ex);
    HotelAvailabilitiesExceptionResponse errors = new HotelAvailabilitiesExceptionResponse(ex.getStatus(),
        ex.getMessage());
    return new ResponseEntity<>(errors, ex.getStatus());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<HotelAvailabilitiesExceptionResponse> handleGenericException(final Exception ex,
      final HttpServletRequest request) {
    log.error("Error: ", ex);
    HotelAvailabilitiesExceptionResponse errors = new HotelAvailabilitiesExceptionResponse(
        HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
      final HttpHeaders headers, final HttpStatusCode status,
      final WebRequest request) {
    log.error("Validation error: ", ex);
    List<String> errors = new ArrayList<>();
    ex.getBindingResult().getFieldErrors().forEach(fieldError ->
        errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage()));

    ex.getBindingResult().getGlobalErrors().forEach(globalError ->
        errors.add(globalError.getObjectName() + ": " + globalError.getDefaultMessage()));

    HotelAvailabilitiesExceptionResponse exceptionResponse = new HotelAvailabilitiesExceptionResponse(
        HttpStatus.BAD_REQUEST, errors);
    return handleExceptionInternal(ex, exceptionResponse, headers, exceptionResponse.getStatus(), request);
  }

}
