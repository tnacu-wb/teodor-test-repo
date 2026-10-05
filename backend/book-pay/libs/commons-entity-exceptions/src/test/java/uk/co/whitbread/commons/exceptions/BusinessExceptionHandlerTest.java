package uk.co.whitbread.commons.exceptions;

import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.commons.exceptions.advice.BusinessExceptionHandler;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBusinessValidationException;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
public class BusinessExceptionHandlerTest {

  private static final String DEBUG_ERROR_MSG = "Debug Http 500 exception thrown";
  private static final String ERROR_MSG = "a.b.c.d";
  private static final int ERROR_CODE = 700;

  @Test
  public void shouldHandle500HttpException() {
    BusinessExceptionHandler advice = new BusinessExceptionHandler();
    Test500HttpException exception = new Test500HttpException(ERROR_MSG, DEBUG_ERROR_MSG,
        ERROR_CODE);

    ResponseEntity<ErrorResponse> response = advice.handleInternalServerException(exception);

    Assertions.assertEquals(response.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getErrCode(), ERROR_CODE);
    Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getGlobalErrTextTemplate(),
        ERROR_MSG);
    Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getDebugMessage(),
        DEBUG_ERROR_MSG);
  }

  @Test
  public void shouldHandle400BadRequestException() {
    BusinessExceptionHandler advice = new BusinessExceptionHandler();
    Test400BadRequestException exception = new Test400BadRequestException(ERROR_MSG,
        DEBUG_ERROR_MSG,
        ERROR_CODE);

    ResponseEntity<ErrorResponse> response = advice.handleBadRequestException(exception);

    Assertions.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getErrCode(), ERROR_CODE);
    Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getGlobalErrTextTemplate(),
        ERROR_MSG);
    Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getDebugMessage(),
        DEBUG_ERROR_MSG);
  }

  @Test
  public void shouldHandleBusinessValidation409HttpException() {
    BusinessExceptionHandler advice = new BusinessExceptionHandler();
    Map<String, String> validationDetails = new HashMap<>();
    validationDetails.put("element1", "element.one.error");
    ErrorResponse errorResponse = ErrorResponse.builder().build();
    errorResponse.addValidationError("element1", "element.one.error");

    Test409HttpBusinessValidationException exception = new Test409HttpBusinessValidationException(
        ERROR_MSG, DEBUG_ERROR_MSG, validationDetails, ERROR_CODE);

    ResponseEntity<ErrorResponse> response = advice.handleValidationException(exception);

    Assertions.assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
    Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getErrCode(), ERROR_CODE);
    Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getGlobalErrTextTemplate(),
        ERROR_MSG);
    Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getDebugMessage(),
        DEBUG_ERROR_MSG);
    Assertions.assertEquals(Objects.requireNonNull(response.getBody()).getDetails(),
        errorResponse.getDetails());
  }


  @Getter
  static class Test400BadRequestException extends AbstractBadRequestException {

    public Test400BadRequestException(String globalErrText, String debugMessage, int errorCode) {
      super(globalErrText, debugMessage, errorCode);
    }
  }

  @Getter
  static class Test409HttpBusinessValidationException extends AbstractBusinessValidationException {

    public Test409HttpBusinessValidationException(String globalErrText, String debugMessage,
        Map<String, String> validationErrors, int errorCode) {
      super(globalErrText, debugMessage, validationErrors, errorCode);
    }
  }

  @Getter
  static class Test500HttpException extends AbstractInternalException {

    public Test500HttpException(String globalErrText, String debugMessage, int errorCode) {
      super(globalErrText, debugMessage, errorCode);
    }
  }

}
