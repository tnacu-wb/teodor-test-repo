package uk.co.whitbread.payment.orchestrator.domain.logic;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransWebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.WebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.PaymentWorkflowPort;

@ExtendWith(MockitoExtension.class)
class WebhookInPortImplTest {

  private static final String BASKET_ID = "basket-123";
  private static final String TRANSACTION_ID = "190410112056083383";

  @Mock
  private PaymentWorkflowPort paymentWorkflowPort;

  @InjectMocks
  private WebhookInPortImpl underTest;

  private static WebhookPayload authorizedPayload() {
    return new DatatransWebhookPayload(
        TRANSACTION_ID,
        "1100012345",
        "authorized",
        "GBP",
        "PI-12345678",
        "VIS",
        9900,
        null,
        null,
        null,
        null,
        null);
  }

  @Nested
  class HandleWebhook {

    @Test
    void withBasketId_delegatesToWorkflowPort() {
      var payload = authorizedPayload();

      underTest.handleWebhook(BASKET_ID, payload);

      verify(paymentWorkflowPort).signalWebhookReceived(BASKET_ID, payload);
    }

    @Test
    void withNullPayload_stillDelegatesToWorkflowPort() {
      underTest.handleWebhook(BASKET_ID, null);

      verify(paymentWorkflowPort).signalWebhookReceived(BASKET_ID, null);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "\t"})
    void withMissingBasketId_isIgnored(String basketId) {
      underTest.handleWebhook(basketId, authorizedPayload());

      verifyNoInteractions(paymentWorkflowPort);
    }

    @Test
    void withMissingBasketIdAndNullPayload_isIgnored() {
      underTest.handleWebhook(null, null);

      verifyNoInteractions(paymentWorkflowPort);
    }
  }
}
