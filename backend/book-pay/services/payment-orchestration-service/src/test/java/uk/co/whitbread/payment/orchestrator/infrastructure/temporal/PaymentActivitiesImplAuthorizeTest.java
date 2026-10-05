package uk.co.whitbread.payment.orchestrator.infrastructure.temporal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransAuthorizeResponse;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransCardInfo;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.DatatransOutPort;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.PaymentEventPublisherPort;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.PaymentMethodOutPort;

/**
 * Unit tests for authorize-related activities in {@link PaymentActivitiesImpl}.
 *
 * <p>Verifies that delegate activities call the correct out-port methods and
 * placeholder activities complete without exception.
 */
@ExtendWith(MockitoExtension.class)
class PaymentActivitiesImplAuthorizeTest {

  @Mock
  private BasketOutPort basketOutPort;

  @Mock
  private PaymentMethodOutPort paymentMethodOutPort;

  @Mock
  private DatatransOutPort datatransOutPort;

  @Mock
  private PaymentEventPublisherPort paymentEventPublisher;

  @InjectMocks
  private PaymentActivitiesImpl underTest;

  @Nested
  class AuthorizeTransaction {

    @Test
    void delegatesToDatatransOutPortWithCorrectTransactionId() {
      var expectedResponse = new DatatransAuthorizeResponse(
          "txn-abc-123", "authorized", "auth-code",
          new DatatransCardInfo("alias", "424242xxxxxx4242", "12", "25"), "VIS");
      when(datatransOutPort.authorizeTransaction("txn-abc-123", "basket-ref", 8600L, "deWB-HARHOR"))
          .thenReturn(expectedResponse);

      DatatransAuthorizeResponse result =
          underTest.authorizeTransaction("txn-abc-123", "basket-ref", 8600L, "deWB-HARHOR");

      verify(datatransOutPort).authorizeTransaction("txn-abc-123", "basket-ref", 8600L, "deWB-HARHOR");
      assertThat(result).isEqualTo(expectedResponse);
    }
  }

  @Nested
  class SettleTransaction {

    @Test
    void delegatesToDatatransOutPortWithCorrectTransactionId() {
      underTest.settleTransaction("txn-settle-456", 8600L, "GBP", "basket-ref", "deWB-HARHOR");

      verify(datatransOutPort).settleTransaction("txn-settle-456", 8600L, "GBP", "basket-ref", "deWB-HARHOR");
    }
  }



}
