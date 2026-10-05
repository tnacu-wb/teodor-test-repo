package uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.payment;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransAuthenticationException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransGatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ThreeDsAuthenticationFailedException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionAlreadyAuthorizedException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatusResponse;
import uk.co.whitbread.payment.orchestrator.domain.ports.primary.PaymentOrchestrationInPort;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.PaymentGlobalExceptionHandler;
import uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.PaymentExceptionHandler;

/**
 * Unit tests for the PaymentController authorize endpoint (POST /api/payments/authorize).
 *
 * <p>Validates: Requirements 5.4
 */
@ExtendWith(MockitoExtension.class)
class PaymentControllerAuthorizeTest {

  private MockMvc mockMvc;

  @Mock
  private PaymentOrchestrationInPort paymentOrchestrationInPort;

  private static final String AUTHORIZE_URL = "/api/payments/authorize";

  @BeforeEach
  void setUp() {
    PaymentController controller = new PaymentController(paymentOrchestrationInPort);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        // Same order the application resolves them in: PaymentGlobalExceptionHandler is
        // @Order(LOWEST_PRECEDENCE) because its RuntimeException catch-all would otherwise
        // swallow the typed PaymentInitializationException. MockMvc's standalone builder
        // honours argument order rather than @Order, so it is spelled out here.
        .setControllerAdvice(new PaymentExceptionHandler(), new PaymentGlobalExceptionHandler())
        .build();
  }

  @Nested
  class SuccessfulAuthorization {

    @Test
    void validRequest_returns200WithAuthorizeResult() throws Exception {
      when(paymentOrchestrationInPort.authorizePayment(
          "AQN-147756bb-bb71-4842-959a-2efe87e378ed"))
          .thenReturn(new AuthorizeResult(true, null, null));

      String validRequest = """
          {"basketId": "AQN-147756bb-bb71-4842-959a-2efe87e378ed"}""";

      mockMvc.perform(post(AUTHORIZE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(validRequest))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.errorCode").doesNotExist())
          .andExpect(jsonPath("$.errorMessage").doesNotExist());
    }
  }

  @Nested
  class StillProcessing {

    @Test
    void authorizationPending_returns202TellingCallerToPoll() throws Exception {
      when(paymentOrchestrationInPort.authorizePayment(
          "AQN-147756bb-bb71-4842-959a-2efe87e378ed"))
          .thenReturn(new AuthorizeResult(false,
              PaymentErrorCode.AUTHORIZATION_PENDING,
              PaymentErrorCode.AUTHORIZATION_PENDING.getErrorMessage()));

      String validRequest = """
          {"basketId": "AQN-147756bb-bb71-4842-959a-2efe87e378ed"}""";

      // 202, not an error status: the authorization is committed and still running, so the
      // customer must not be told it failed and the client must not retry the charge.
      mockMvc.perform(post(AUTHORIZE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(validRequest))
          .andExpect(status().isAccepted())
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.errorCode").value("AUTHORIZATION_PENDING"))
          .andExpect(jsonPath("$.errorMessage")
              .value(PaymentErrorCode.AUTHORIZATION_PENDING.getErrorMessage()));
    }
  }

  @Nested
  class PaymentStatusEndpoint {

    private static final String STATUS_URL =
        "/api/payments/AQN-147756bb-bb71-4842-959a-2efe87e378ed/status";

    @Test
    void returns200WithStatusAndAuthorizeResult() throws Exception {
      when(paymentOrchestrationInPort.getPaymentStatus(
          "AQN-147756bb-bb71-4842-959a-2efe87e378ed"))
          .thenReturn(new PaymentStatusResponse(
              PaymentStatus.AUTHORIZED, new AuthorizeResult(true, null, null)));

      mockMvc.perform(get(STATUS_URL))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.paymentStatus").value("AUTHORIZED"))
          .andExpect(jsonPath("$.authorizeResult.success").value(true));
    }

    @Test
    void returns200WithNullAuthorizeResultWhileStillProcessing() throws Exception {
      when(paymentOrchestrationInPort.getPaymentStatus(anyString()))
          .thenReturn(new PaymentStatusResponse(PaymentStatus.INITIALIZED, null));

      mockMvc.perform(get(STATUS_URL))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.paymentStatus").value("INITIALIZED"))
          .andExpect(jsonPath("$.authorizeResult").doesNotExist());
    }

    @Test
    void unknownBasket_returns404WithBasketNotFoundCode() throws Exception {
      doThrow(new BasketNotFoundException("No payment workflow found for basket"))
          .when(paymentOrchestrationInPort).getPaymentStatus(anyString());

      mockMvc.perform(get(STATUS_URL))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.error.code").value("BASKET_NOT_FOUND"));
    }
  }

  @Nested
  class ValidationErrors {

    @ParameterizedTest
    @ValueSource(strings = {
        "{\"basketId\": \"   \"}",
        "{\"basketId\": \"invalid-format\"}"
    })
    void invalidFields_return400WithInvalidRequestCode(String invalidRequest) throws Exception {
      mockMvc.perform(post(AUTHORIZE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(invalidRequest))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
          .andExpect(jsonPath("$.error.message").isNotEmpty());
    }

    @Test
    void emptyRequestBody_returns400() throws Exception {
      mockMvc.perform(post(AUTHORIZE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content("{}"))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }
  }

  @Nested
  class DomainErrors {

    @Test
    void datatransAuthenticationException_returns502WithGatewayAuthenticationFailedCode()
        throws Exception {
      doThrow(new DatatransAuthenticationException(
          "Datatrans rejected merchant credentials during authorize: 401 UNAUTHORIZED"))
          .when(paymentOrchestrationInPort).authorizePayment(anyString());

      String validRequest = """
          {"basketId": "AQN-147756bb-bb71-4842-959a-2efe87e378ed"}""";

      // Our merchant credentials failed at the gateway, not the caller's — a 401 here would
      // send clients off refreshing a token that was never the problem.
      mockMvc.perform(post(AUTHORIZE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(validRequest))
          .andExpect(status().isBadGateway())
          .andExpect(jsonPath("$.error.code").value("GATEWAY_AUTHENTICATION_FAILED"))
          .andExpect(jsonPath("$.error.message")
              .value(PaymentErrorCode.GATEWAY_AUTHENTICATION_FAILED.getErrorMessage()));
    }

    @Test
    void workflowGatewayAuthenticationFailure_returns502WithGatewayAuthenticationFailedCode()
        throws Exception {
      when(paymentOrchestrationInPort.authorizePayment(anyString()))
          .thenReturn(new AuthorizeResult(false, PaymentErrorCode.GATEWAY_AUTHENTICATION_FAILED,
              PaymentErrorCode.GATEWAY_AUTHENTICATION_FAILED.getErrorMessage()));

      String validRequest = """
          {"basketId": "AQN-147756bb-bb71-4842-959a-2efe87e378ed"}""";

      mockMvc.perform(post(AUTHORIZE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(validRequest))
          .andExpect(status().isBadGateway())
          .andExpect(jsonPath("$.error.code").value("GATEWAY_AUTHENTICATION_FAILED"));
    }

    @Test
    void transactionNotFoundException_returns404WithTransactionNotFoundCode() throws Exception {
      doThrow(new TransactionNotFoundException("Transaction not found or expired"))
          .when(paymentOrchestrationInPort).authorizePayment(anyString());

      String validRequest = """
          {"basketId": "AQN-147756bb-bb71-4842-959a-2efe87e378ed"}""";

      mockMvc.perform(post(AUTHORIZE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(validRequest))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.error.code").value("TRANSACTION_NOT_FOUND"))
          .andExpect(jsonPath("$.error.message").value("Transaction not found or expired"));
    }

    @Test
    void workflowNotFoundResult_returns404WithTransactionNotFoundCode_not502() throws Exception {
      // The adapter reports a missing workflow (authorize before init, or already closed) as
      // an AuthorizeResult carrying the string code TRANSACTION_NOT_FOUND — which is not a
      // PaymentErrorCode constant. It must reach the dedicated 404 mapping, not fall through
      // the generic switch to GATEWAY_ERROR/502: that would page ops for a caller mistake and
      // invite retries that no gateway recovery can satisfy.
      when(paymentOrchestrationInPort.authorizePayment(anyString()))
          .thenReturn(new AuthorizeResult(false, PaymentErrorCode.TRANSACTION_NOT_FOUND,
              "No payment workflow found for basket AQN-147756bb-bb71-4842-959a-2efe87e378ed"));

      String validRequest = """
          {"basketId": "AQN-147756bb-bb71-4842-959a-2efe87e378ed"}""";

      mockMvc.perform(post(AUTHORIZE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(validRequest))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.error.code").value("TRANSACTION_NOT_FOUND"))
          .andExpect(jsonPath("$.error.message").value(
              "No payment workflow found for basket AQN-147756bb-bb71-4842-959a-2efe87e378ed"));
    }

    @Test
    void transactionAlreadyAuthorizedException_returns409WithAlreadyAuthorizedCode() throws Exception {
      doThrow(new TransactionAlreadyAuthorizedException("Transaction already authorized"))
          .when(paymentOrchestrationInPort).authorizePayment(anyString());

      String validRequest = """
          {"basketId": "AQN-147756bb-bb71-4842-959a-2efe87e378ed"}""";

      mockMvc.perform(post(AUTHORIZE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(validRequest))
          .andExpect(status().isConflict())
          .andExpect(jsonPath("$.error.code").value("TRANSACTION_ALREADY_AUTHORIZED"))
          .andExpect(jsonPath("$.error.message").value("Transaction already authorized"));
    }

    @Test
    void threeDsAuthenticationFailedException_returns422With3dsAuthFailedCode() throws Exception {
      doThrow(new ThreeDsAuthenticationFailedException("3DS authentication failed"))
          .when(paymentOrchestrationInPort).authorizePayment(anyString());

      String validRequest = """
          {"basketId": "AQN-147756bb-bb71-4842-959a-2efe87e378ed"}""";

      mockMvc.perform(post(AUTHORIZE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(validRequest))
          .andExpect(status().isUnprocessableContent())
          .andExpect(jsonPath("$.error.code").value("3DS_AUTHENTICATION_FAILED"))
          .andExpect(jsonPath("$.error.message").value("3DS authentication failed"));
    }

    @Test
    void datatransGatewayException_returns502WithGatewayErrorCode() throws Exception {
      doThrow(new DatatransGatewayException("Datatrans returned 500"))
          .when(paymentOrchestrationInPort).authorizePayment(anyString());

      String validRequest = """
          {"basketId": "AQN-147756bb-bb71-4842-959a-2efe87e378ed"}""";

      mockMvc.perform(post(AUTHORIZE_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(validRequest))
          .andExpect(status().isBadGateway())
          .andExpect(jsonPath("$.error.code").value("GATEWAY_ERROR"))
          .andExpect(jsonPath("$.error.message").value("Datatrans returned 500"));
    }
  }
}
