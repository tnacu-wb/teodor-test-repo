package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.errorresponse.ErrorDetail;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.errorresponse.ErrorResponse;

@ExtendWith(MockitoExtension.class)
class ControllerAdviceTest {

  @InjectMocks
  private ControllerAdvice controllerAdvice;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void testHandleBadRequest() throws JsonProcessingException {
    HttpClientErrorException httpClientErrorException = mock(HttpClientErrorException.class);
    String exceptionMessage = "400 Bad Request {\"message\":\"Sample Error\",\"errors\":[{\"message\":\"Error Message\"}]}";
    when(httpClientErrorException.getMessage()).thenReturn(exceptionMessage);
    ResponseEntity<ErrorResponse> responseEntity = controllerAdvice.handleBadRequest(
        httpClientErrorException);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    ErrorResponse errorResponse = responseEntity.getBody();
    assertEquals("Bad Request", errorResponse.getMessage());
    assertEquals(1, errorResponse.getErrors().size());
    ErrorDetail errorDetail = errorResponse.getErrors().get(0);
    assertEquals("Error", errorDetail.getField());
    assertEquals("Error Message", errorDetail.getMessage());
  }

  @Test
  void testHandleBadRequest1() throws JsonProcessingException {
    HttpClientErrorException httpClientErrorException = mock(HttpClientErrorException.class);
    String exceptionMessage = "400 Bad Request {\"message\":\"Sample Error\",\"\":[{\"message\":\"Error Message\"}]}";
    when(httpClientErrorException.getMessage()).thenReturn(exceptionMessage);
    ResponseEntity<ErrorResponse> responseEntity = controllerAdvice.handleBadRequest(
        httpClientErrorException);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }


  @Test
  void testHandleNotFoundException() {
    HttpClientErrorException httpClientErrorException = mock(HttpClientErrorException.class);
    ResponseEntity<ErrorResponse> responseEntity = controllerAdvice.handleNotFound(
        httpClientErrorException);
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    ErrorResponse errorResponse = responseEntity.getBody();
    assertEquals("Resource Not Found", errorResponse.getMessage());
    assertEquals(1, errorResponse.getErrors().size());
    ErrorDetail errorDetail = errorResponse.getErrors().get(0);
    assertEquals("The requested resource is not found.", errorDetail.getMessage());
    assertEquals("path", errorDetail.getField());
  }

  @Test
  void testHandleServerError() {
    Throwable throwable = new Throwable("Sample error message");

    ResponseEntity<ErrorResponse> responseEntity = controllerAdvice.handleServerError(throwable);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    ErrorResponse errorResponse = responseEntity.getBody();
    assertEquals("Internal Server Error", errorResponse.getMessage());
    List<ErrorDetail> errorDetails = errorResponse.getErrors();
    assertEquals(1, errorDetails.size());
    assertEquals("Error", errorDetails.get(0).getField());
    assertEquals("Sample error message", errorDetails.get(0).getMessage());
  }

}
