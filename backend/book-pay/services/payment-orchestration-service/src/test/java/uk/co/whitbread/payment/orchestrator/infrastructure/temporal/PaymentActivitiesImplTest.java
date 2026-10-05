package uk.co.whitbread.payment.orchestrator.infrastructure.temporal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransGatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ServiceUnavailableException;
import uk.co.whitbread.payment.orchestrator.domain.model.BasketStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransCardInfo;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransMobileSdkRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransSecureFieldsRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransTransactionStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentAuthorisedEvent;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentOption;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentMethodValidationResult;
import uk.co.whitbread.payment.orchestrator.domain.model.Reservation;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.DatatransOutPort;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.PaymentEventPublisherPort;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.PaymentMethodOutPort;

@ExtendWith(MockitoExtension.class)
class PaymentActivitiesImplTest {

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
    void delegatesToDatatransOutPort() {
      underTest.authorizeTransaction("txn-123", "bsk-ref", 8600L, "deWB-HARHOR");

      verify(datatransOutPort).authorizeTransaction("txn-123", "bsk-ref", 8600L, "deWB-HARHOR");
    }
  }

  @Nested
  class GetTransactionStatus {

    @Test
    void delegatesToDatatransOutPortAndReturnsResult() {
      var expected = new DatatransTransactionStatus(
          "txn-123", "authorized", "GBP", 8600, "auth-code",
          new DatatransCardInfo("card-alias", "424242xxxxxx4242", "12", "28"), "VIS", "ARH1234567");
      when(datatransOutPort.getTransactionStatus("txn-123", "deWB-HARHOR")).thenReturn(expected);

      DatatransTransactionStatus result = underTest.getTransactionStatus("txn-123", "deWB-HARHOR");

      assertThat(result).isEqualTo(expected);
      verify(datatransOutPort).getTransactionStatus("txn-123", "deWB-HARHOR");
    }
  }


  @Nested
  class GetReservation {

    @Test
    void delegatesToBasketOutPort() {
      var reservation = new Reservation(
          "basket-1", "HOTEL-001", new BigDecimal("86.00"),
          "GBP", "PI-123456789", "PI-123456789", "PI");
      when(basketOutPort.getReservation("basket-1")).thenReturn(reservation);

      Reservation result = underTest.getReservation("basket-1");

      assertThat(result).isEqualTo(reservation);
    }
  }

  @Nested
  class GetBasketStatus {

    @Test
    void delegatesToBasketOutPort() {
      when(basketOutPort.getBasketStatus("basket-1")).thenReturn(BasketStatus.COMPLETED);

      assertThat(underTest.getBasketStatus("basket-1")).isEqualTo(BasketStatus.COMPLETED);
    }
  }

  @Nested
  class ValidatePaymentMethods {

    @Test
    void delegatesToPaymentMethodOutPortWithProvidedFields() {
      var expected = new PaymentMethodValidationResult(true, List.of("VIS", "ECA"));
      when(paymentMethodOutPort.validatePaymentMethods("basket-1", "gb", "en", "LEISURE", "PI"))
          .thenReturn(expected);

      PaymentMethodValidationResult result =
          underTest.validatePaymentMethods("basket-1", "HOTEL-001", "gb", "en", "LEISURE", "PI");

      assertThat(result).isEqualTo(expected);
      verify(paymentMethodOutPort).validatePaymentMethods("basket-1", "gb", "en", "LEISURE", "PI");
    }

    @Test
    void delegatesToPaymentMethodOutPortWithGermanFields() {
      var expected = new PaymentMethodValidationResult(true, List.of("VIS", "ECA", "AMX"));
      when(paymentMethodOutPort.validatePaymentMethods("basket-2", "de", "de", "BUSINESS", "APPS_IOS"))
          .thenReturn(expected);

      PaymentMethodValidationResult result =
          underTest.validatePaymentMethods("basket-2", "HOTEL-002", "de", "de", "BUSINESS", "APPS_IOS");

      assertThat(result).isEqualTo(expected);
      verify(paymentMethodOutPort).validatePaymentMethods("basket-2", "de", "de", "BUSINESS", "APPS_IOS");
    }
  }

  @Nested
  class InitDatatransSecureFields {

    @Test
    void delegatesToDatatransOutPort() {
      var request = new DatatransSecureFieldsRequest(8600L, "GBP",
          "https://example.com/return", "deWB-HOTEL", "POST");
      when(datatransOutPort.initSecureFields(request)).thenReturn("sf-txn-123");

      String result = underTest.initDatatransSecureFields(request);

      assertThat(result).isEqualTo("sf-txn-123");
    }
  }

  @Nested
  class InitMobileSdkTransaction {

    private final DatatransMobileSdkRequest request = new DatatransMobileSdkRequest(
        8600L, "GBP", "PI-basket-1", List.of("VIS", "ECA"), "deWB-HOTEL");

    @Test
    void successfulCall_returnsTransactionId() {
      when(datatransOutPort.initMobileSdk(request)).thenReturn("txn-mobile-123");

      String result = underTest.initMobileSdkTransaction(request);

      assertThat(result).isEqualTo("txn-mobile-123");
    }

    @Test
    void datatransGatewayException_rethrown() {
      when(datatransOutPort.initMobileSdk(request))
          .thenThrow(new DatatransGatewayException("Datatrans v2 returned 400"));

      assertThatThrownBy(() -> underTest.initMobileSdkTransaction(request))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessage("Datatrans v2 returned 400");
    }

    @Test
    void serviceUnavailableException_rethrown() {
      when(datatransOutPort.initMobileSdk(request))
          .thenThrow(new ServiceUnavailableException("Datatrans is unreachable"));

      assertThatThrownBy(() -> underTest.initMobileSdkTransaction(request))
          .isInstanceOf(ServiceUnavailableException.class)
          .hasMessage("Datatrans is unreachable");
    }

    @Test
    void unexpectedException_wrappedInServiceUnavailable() {
      when(datatransOutPort.initMobileSdk(request))
          .thenThrow(new RuntimeException("Something unexpected"));

      assertThatThrownBy(() -> underTest.initMobileSdkTransaction(request))
          .isInstanceOf(ServiceUnavailableException.class)
          .hasMessageContaining("Something unexpected");
    }
  }

  @Nested
  class PublishAuthorisedPaymentEvent {

    @Test
    void constructsEventAndPublishes() {
      underTest.publishAuthorisedPaymentEvent(
          "basket-1", "txn-123", "alias-token", 8600, "GBP", "VIS", "4242", "12/28",
          PaymentOption.PAY_NOW, "de");

      ArgumentCaptor<PaymentAuthorisedEvent> captor =
          ArgumentCaptor.forClass(PaymentAuthorisedEvent.class);
      verify(paymentEventPublisher).publish(captor.capture());

      PaymentAuthorisedEvent event = captor.getValue();
      assertThat(event.basketId()).isEqualTo("basket-1");
      assertThat(event.transactionId()).isEqualTo("txn-123");
      assertThat(event.paymentProvider()).isEqualTo("datatrans");
      assertThat(event.paymentMethod()).isEqualTo("VIS");
      assertThat(event.cardAlias()).isEqualTo("alias-token");
      assertThat(event.last4Digits()).isEqualTo("4242");
      assertThat(event.expiry()).isEqualTo("12/28");
      assertThat(event.authorizedAmount()).isEqualTo(8600);
      assertThat(event.currency()).isEqualTo("GBP");
      // The status is a wire constant, not derived from workflow state: this event exists
      // only for the AUTHORIZED transition.
      assertThat(event.paymentStatus()).isEqualTo("AUTHORIZED");
      assertThat(event.language()).isEqualTo("de");
    }

    @Test
    void nullCardDetails_publishesEventWithNulls() {
      underTest.publishAuthorisedPaymentEvent(
          "basket-1", "txn-123", null, 8600L, "GBP", null, null, null,
          PaymentOption.PAY_NOW, null);

      ArgumentCaptor<PaymentAuthorisedEvent> captor =
          ArgumentCaptor.forClass(PaymentAuthorisedEvent.class);
      verify(paymentEventPublisher).publish(captor.capture());

      PaymentAuthorisedEvent event = captor.getValue();
      assertThat(event.basketId()).isEqualTo("basket-1");
      assertThat(event.transactionId()).isEqualTo("txn-123");
      assertThat(event.paymentProvider()).isEqualTo("datatrans");
      assertThat(event.cardAlias()).isNull();
      assertThat(event.authorizedAmount()).isEqualTo(8600L);
      assertThat(event.paymentMethod()).isNull();
      assertThat(event.last4Digits()).isNull();
      assertThat(event.expiry()).isNull();
    }
  }
}
