package uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ServiceUnavailableException;

class PaymentGlobalExceptionHandlerTest {

  private final PaymentGlobalExceptionHandler underTest = new PaymentGlobalExceptionHandler();

  @Nested
  class ValidationErrors {

    /**
     * A request wrong in three places is wrong in three places. Reporting only the first sends
     * the client round a fix-one-resubmit loop for what is a single bad payload.
     */
    @Test
    void reportsEveryFieldErrorInStableFieldOrder() {
      MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
      when(ex.getFieldErrors()).thenReturn(List.of(
          fieldError("returnUrl", "must be HTTPS"),
          fieldError("basketId", "must not be blank"),
          fieldError("country", "must be a two-letter code")));

      ResponseEntity<PaymentGlobalExceptionHandler.GlobalErrorResponse> response =
          underTest.handleValidationException(ex);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().error().code()).isEqualTo("INVALID_REQUEST");
      assertThat(response.getBody().error().message()).isEqualTo(
          "basketId: must not be blank; "
              + "country: must be a two-letter code; "
              + "returnUrl: must be HTTPS");
    }

    @Test
    void fallsBackToAGenericMessageWhenNoFieldErrorIsPresent() {
      MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
      when(ex.getFieldErrors()).thenReturn(List.of());

      ResponseEntity<PaymentGlobalExceptionHandler.GlobalErrorResponse> response =
          underTest.handleValidationException(ex);

      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().error().message()).isEqualTo("Invalid request");
    }

    private FieldError fieldError(String field, String message) {
      return new FieldError("paymentInitRequest", field, message);
    }
  }

  @Nested
  class CatchAll {

    /**
     * An unrecognised RuntimeException is a bug here, not a downstream outage. Answering 503
     * would tell clients to retry something that will fail identically and point alerting at
     * the wrong system.
     */
    @Test
    void answersUnexpectedRuntimeExceptionsWith500AndNoInternalDetail() {
      ResponseEntity<PaymentGlobalExceptionHandler.GlobalErrorResponse> response =
          underTest.handleUnexpectedException(
              new IllegalStateException("jdbc://secret-host password=hunter2"));

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().error().code()).isEqualTo("INTERNAL_ERROR");
      assertThat(response.getBody().error().message())
          .isEqualTo("An unexpected error occurred. Please try again.")
          .doesNotContain("hunter2");
    }

    /** 503 stays reserved for a dependency that genuinely cannot be reached. */
    @Test
    void keeps503ForServiceUnavailableException() {
      ResponseEntity<PaymentGlobalExceptionHandler.GlobalErrorResponse> response =
          underTest.handleServiceUnavailable(
              new ServiceUnavailableException("Basket service is unreachable"));

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().error().code()).isEqualTo("SERVICE_UNAVAILABLE");
    }
  }
}
