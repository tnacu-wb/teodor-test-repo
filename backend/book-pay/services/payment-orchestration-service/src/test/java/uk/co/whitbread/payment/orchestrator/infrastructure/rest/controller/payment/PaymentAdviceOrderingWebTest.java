package uk.co.whitbread.payment.orchestrator.infrastructure.rest.controller.payment;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.PaymentInitializationException;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;
import uk.co.whitbread.payment.orchestrator.domain.ports.primary.PaymentOrchestrationInPort;

/**
 * Proves the two {@code @RestControllerAdvice} beans resolve in the right order in a real,
 * component-scanned context.
 *
 * <p>The controller unit tests register the advices by hand via
 * {@code setControllerAdvice(new PaymentExceptionHandler(), new PaymentGlobalExceptionHandler())},
 * which silently fixes the very ordering this test exists to check: Spring stops at the first
 * advice with any matching handler, and {@code PaymentGlobalExceptionHandler}'s
 * {@code RuntimeException} catch-all matches {@link PaymentInitializationException} too. Before
 * {@code PaymentExceptionHandler} carried an explicit {@code @Order(0)}, both advices tied at
 * {@code LOWEST_PRECEDENCE} and bean registration order let the catch-all win — every typed
 * payment error degraded to a generic 500 in the deployed app while the hand-wired unit tests
 * stayed green. This test loads the advices the way production does, so a regression of the
 * ordering fails here.
 *
 * <p>The application's wide {@code scanBasePackages = "uk.co.whitbread"} (needed so the logging
 * library's {@code FluentdLogger} is picked up at runtime) would otherwise sweep the shared
 * {@code commons-logging} debug advices into this slice; they require an auto-config-only bean a
 * web slice does not provide. {@code src/test/resources/application.yml} sets
 * {@code logging.configuration.debug.enabled=false}, so those advices are never candidate beans in
 * tests and the slice loads cleanly.
 */
@WebMvcTest(PaymentController.class)
class PaymentAdviceOrderingWebTest {

  private static final String VALID_BASKET_ID = "AQN-147756bb-bb71-4842-959a-2efe87e378ed";

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private PaymentOrchestrationInPort paymentOrchestrationInPort;

  @Test
  @DisplayName("A typed payment error reaches PaymentExceptionHandler, not the 500 catch-all")
  void typedPaymentError_mapsToItsTypedStatus_notTheCatchAll() throws Exception {
    when(paymentOrchestrationInPort.authorizePayment(anyString()))
        .thenThrow(new PaymentInitializationException(PaymentErrorCode.BASKET_NOT_FOUND));

    mockMvc.perform(post("/api/payments/authorize")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"basketId\":\"" + VALID_BASKET_ID + "\"}"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("BASKET_NOT_FOUND"));
  }

  @Test
  @DisplayName("An expired transaction maps to 410 through the real advice ordering")
  void expiredTransaction_mapsToGone() throws Exception {
    when(paymentOrchestrationInPort.authorizePayment(anyString()))
        .thenThrow(new PaymentInitializationException(PaymentErrorCode.TRANSACTION_EXPIRED));

    mockMvc.perform(post("/api/payments/authorize")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"basketId\":\"" + VALID_BASKET_ID + "\"}"))
        .andExpect(status().isGone())
        .andExpect(jsonPath("$.error.code").value("TRANSACTION_EXPIRED"));
  }

  @Test
  @DisplayName("A genuinely unexpected exception still lands in the 500 catch-all")
  void unexpectedException_stillHitsTheCatchAll() throws Exception {
    when(paymentOrchestrationInPort.authorizePayment(anyString()))
        .thenThrow(new IllegalStateException("boom"));

    mockMvc.perform(post("/api/payments/authorize")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"basketId\":\"" + VALID_BASKET_ID + "\"}"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error.code").value("INTERNAL_ERROR"));
  }
}
