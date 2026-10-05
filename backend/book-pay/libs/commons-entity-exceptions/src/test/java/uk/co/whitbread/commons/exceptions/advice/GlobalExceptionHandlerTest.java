package uk.co.whitbread.commons.exceptions.advice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import uk.co.whitbread.commons.exceptions.enums.ValidationError;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

  @Mock
  private WebRequest mockRequest;

  @Test
  public void shouldHandleNoResourceFoundExceptionAs404() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    NoResourceFoundException exception = mock(NoResourceFoundException.class);

    ResponseEntity<Object> response = handler.handleNoResourceFoundException(exception,
        HttpHeaders.EMPTY, HttpStatusCode.valueOf(404), mockRequest);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    ErrorResponse body = (ErrorResponse) response.getBody();
    assertEquals(ValidationError.NO_RESOURCE_FOUND_EXCEPTION.getErrorCode(),
        Objects.requireNonNull(body).getErrCode());
    assertEquals(ValidationError.NO_RESOURCE_FOUND_EXCEPTION.getMessage(),
        body.getGlobalErrTextTemplate());
  }

  @Test
  public void shouldHandleNoHandlerFoundExceptionAs404() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    NoHandlerFoundException exception = mock(NoHandlerFoundException.class);

    ResponseEntity<Object> response = handler.handleNoHandlerFoundException(exception,
        HttpHeaders.EMPTY, HttpStatusCode.valueOf(404), mockRequest);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    ErrorResponse body = (ErrorResponse) response.getBody();
    assertEquals(ValidationError.NO_HANDLER_FOUND_EXCEPTION.getErrorCode(),
        Objects.requireNonNull(body).getErrCode());
    assertEquals(ValidationError.NO_HANDLER_FOUND_EXCEPTION.getMessage(),
        body.getGlobalErrTextTemplate());
  }

  @Test
  public void shouldHandleGenericExceptionAs500() {
    GlobalExceptionHandler handler = new GlobalExceptionHandler();
    Exception exception = new Exception("Test generic exception");

    ResponseEntity<Object> response = handler.handleAllUncaughtException(exception, mockRequest);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    ErrorResponse body = (ErrorResponse) response.getBody();
    assertEquals(ValidationError.GENERIC_EXCEPTION.getErrorCode(),
        Objects.requireNonNull(body).getErrCode());
    assertEquals(ValidationError.GENERIC_EXCEPTION.getMessage(), body.getGlobalErrTextTemplate());
  }
}
