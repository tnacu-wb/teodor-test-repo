package uk.co.whitbread.basket.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.logic.config.DistributionProperties;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.email.out.EmailNotificationEventType;
import uk.co.whitbread.basket.domain.model.email.out.TransactionData;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ProviderResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ThreeCResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.CurrencyAmountType;
import uk.co.whitbread.basket.domain.model.reservation.out.Deposits;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositsResponse;
import uk.co.whitbread.basket.domain.ports.secondary.EmailNotificationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.PaymentOutPort;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceImplTest {

  private static final String EMAIL_ADDRESS = "test@gmail.com";
  private static final String RESERVATION_ID = "123";
  private static final String BASKET_ID = "BKR-ace3a445-ebd1-4404-9315-d3df37a0b464";
  private static final String HOTEL_ID = "LONEUS";

  private static final String BASKET_REF = "BKR12345";
  private static final String PAYMENT_REFERENCE = "123ABC";
  private static final String PAYMENT_REFERENCE_WITH_3DS = "123ABC|1";
  private static final String REFUND_REFERENCE = "ZYX123";
  private static final String CURRENCY_CODE = "EUR";
  public static final String LAST_4_DIGITS = "1234";
  public static final String CARD_SCHEME_ID = "AV";

  @Mock
  private PaymentOutPort paymentOutPort;
  @Mock
  private HotelReservationOutPort reservationOutPort;
  @Mock
  private EmailNotificationOutPort emailNotificationOutPort;
  @Captor
  ArgumentCaptor<TransactionData> captor;

  @InjectMocks
  private EmailNotificationServiceImpl emailService;

  @Mock
  private DistributionProperties distributionProperties;

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Test
  void sendEmailNotification_ifNoDepositsGiven_getDepositsFromReservation() {
    // Arrange
    var basket = mockBasket();

    when(reservationOutPort.getDepositsForReservationId(basket.getHotelId(), RESERVATION_ID))
        .thenReturn(mockDepositsResponse());
    when(paymentOutPort.getPaymentConfirmation(PAYMENT_REFERENCE))
        .thenReturn(mockPaymentResponse());
    when(paymentOutPort.getPaymentConfirmation(REFUND_REFERENCE))
        .thenReturn(mockRefundResponse());

    // Act
    emailService.sendEmailNotificationEvent(basket, EmailNotificationEventType.CANCEL,
        EMAIL_ADDRESS, true);

    // Assert
    verify(reservationOutPort).getDepositsForReservationId(basket.getHotelId(), RESERVATION_ID);
  }

  @Test
  void sendEmailNotification_success() {
    // Arrange
    var basket = mockBasket();

    when(reservationOutPort.getDepositsForReservationId(basket.getHotelId(), RESERVATION_ID))
        .thenReturn(mockDepositsResponse());
    when(paymentOutPort.getPaymentConfirmation(PAYMENT_REFERENCE))
        .thenReturn(mockPaymentResponse());
    when(paymentOutPort.getPaymentConfirmation(REFUND_REFERENCE))
        .thenReturn(mockRefundResponse());

    // Act
    emailService.sendEmailNotificationEvent(basket, EmailNotificationEventType.CANCEL,
        EMAIL_ADDRESS, true);

    // Assert
    verify(emailNotificationOutPort).sendEmailNotificationEvent(any(), any(), any(), captor.capture(), anyList());
    var transactionData =  captor.getValue();
    assertEquals(1, transactionData.getPaymentDetails().size());
    assertEquals(1, transactionData.getRefundPaymentDetails().size());
    var paymentDetails = transactionData.getPaymentDetails().get(0);
    assertEquals(PAYMENT_REFERENCE, paymentDetails.getPaymentId());
    assertTrue(paymentDetails.getCardNo().endsWith(LAST_4_DIGITS));
    assertEquals(CARD_SCHEME_ID, paymentDetails.getCardType());
    assertEquals(BigDecimal.TEN, paymentDetails.getAmount());
    assertEquals(CURRENCY_CODE, paymentDetails.getCurrency());
    var refundDetails = transactionData.getRefundPaymentDetails().get(0);
    assertEquals(Boolean.TRUE, refundDetails.getIsRefundSuccess());
  }

  @Test
  void sendEmailNotification_withDeposits_success() {
    // Arrange
    var basket = mockBasket();

    var deposits = List.of(mockPayment());
    when(paymentOutPort.getPaymentConfirmation(PAYMENT_REFERENCE))
        .thenReturn(mockPaymentResponse());

    // Act
    emailService.sendEmailNotificationEvent(basket, EmailNotificationEventType.CANCEL,
        EMAIL_ADDRESS, false, deposits);

    // Assert
    verify(emailNotificationOutPort).sendEmailNotificationEvent(any(), any(), any(), captor.capture(), anyList());
    var transactionData =  captor.getValue();
    assertEquals(1, transactionData.getPaymentDetails().size());
    var paymentDetails = transactionData.getPaymentDetails().get(0);
    assertEquals(PAYMENT_REFERENCE, paymentDetails.getPaymentId());
    assertTrue(paymentDetails.getCardNo().endsWith(LAST_4_DIGITS));
    assertEquals(CARD_SCHEME_ID, paymentDetails.getCardType());
    assertEquals(BigDecimal.TEN, paymentDetails.getAmount());
    assertEquals(CURRENCY_CODE, paymentDetails.getCurrency());
  }

  /* 3DS Indicator is a status (|1) received from Planet and appended to paymentId
  // then saved as paymentReference in Opera. e.g: (324355224D|1)
  */
  @Test
  void sendEmailNotification_withDeposits_and_3DS_Indicator_success() {
    // Arrange
    var basket = mockBasket();

    var deposits = List.of(mockPaymentWith3DS());
    when(paymentOutPort.getPaymentConfirmation(PAYMENT_REFERENCE))
            .thenReturn(mockPaymentResponse());

    // Act
    emailService.sendEmailNotificationEvent(basket, EmailNotificationEventType.CANCEL,
            EMAIL_ADDRESS, false, deposits);

    // Assert
    verify(emailNotificationOutPort).sendEmailNotificationEvent(any(), any(), any(), captor.capture(), anyList());
    var transactionData =  captor.getValue();
    assertEquals(1, transactionData.getPaymentDetails().size());
    var paymentDetails = transactionData.getPaymentDetails().get(0);
    assertEquals(PAYMENT_REFERENCE, paymentDetails.getPaymentId());
    assertTrue(paymentDetails.getCardNo().endsWith(LAST_4_DIGITS));
    assertEquals(CARD_SCHEME_ID, paymentDetails.getCardType());
    assertEquals(BigDecimal.TEN, paymentDetails.getAmount());
    assertEquals(CURRENCY_CODE, paymentDetails.getCurrency());
  }

  @Test
  void sendEmailNotification_filterOutZeroAmountDeposits() {
    // Arrange
    var basket = mockBasket();
    var deposits = List.of(mockZeroAmountPayment());

    // Act
    emailService.sendEmailNotificationEvent(basket, EmailNotificationEventType.AMEND, EMAIL_ADDRESS, false, deposits);

    // Assert
    verify(emailNotificationOutPort).sendEmailNotificationEvent(any(), any(), any(), captor.capture(), anyList());
    var transactionData =  captor.getValue();
    assertEquals(0, transactionData.getPaymentDetails().size());
    assertEquals(0, transactionData.getRefundPaymentDetails().size());
  }

  @Test
  void sendEmailNotification_failedRefundPaymentDetails() {
    // Arrange
    var basket = mockBasket();

    var deposits = List.of(mockRefund());
    when(paymentOutPort.getPaymentConfirmation(REFUND_REFERENCE))
        .thenReturn(mockRefundResponse());

    // Act
    emailService.sendEmailNotificationEvent(basket, EmailNotificationEventType.AMEND, EMAIL_ADDRESS, true, deposits);

    // Assert
    verify(emailNotificationOutPort).sendEmailNotificationEvent(any(), any(), any(), captor.capture(), anyList());
    var transactionData =  captor.getValue();
    assertEquals(1, transactionData.getRefundPaymentDetails().size());
    assertEquals(Boolean.FALSE, transactionData.getRefundPaymentDetails().get(0).getIsRefundSuccess());
  }

  private static PaymentResponse mockPaymentResponse() {
    return PaymentResponse.builder()
        .paymentId(PAYMENT_REFERENCE)
        .createdOn(new Date())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .last4Digits(LAST_4_DIGITS)
                .cardSchemeId(CARD_SCHEME_ID)
                .build())
            .build())
        .build();
  }

  private static PaymentResponse mockRefundResponse() {
    return PaymentResponse.builder()
        .paymentId(REFUND_REFERENCE)
        .createdOn(new Date())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .last4Digits(LAST_4_DIGITS)
                .cardSchemeId(CARD_SCHEME_ID)
                .build())
            .build())
        .build();
  }

  private static DepositsResponse mockDepositsResponse() {
    return DepositsResponse.builder()
        .deposits(List.of(mockPayment(), mockRefund()))
        .build();
  }

  private static Deposits mockPayment() {
    return Deposits.builder()
        .paymentReference(PAYMENT_REFERENCE)
        .postedAmount(CurrencyAmountType.builder()
            .amount(BigDecimal.TEN)
            .currencyCode(CURRENCY_CODE)
            .build())
        .build();
  }

  private static Deposits mockPaymentWith3DS() {
    return Deposits.builder()
            .paymentReference(PAYMENT_REFERENCE_WITH_3DS)
            .postedAmount(CurrencyAmountType.builder()
                    .amount(BigDecimal.TEN)
                    .currencyCode(CURRENCY_CODE)
                    .build())
            .build();
  }

  private static Deposits mockRefund() {
    return Deposits.builder()
        .paymentReference(REFUND_REFERENCE)
        .postedAmount(CurrencyAmountType.builder()
            .amount(BigDecimal.TEN.negate())
            .currencyCode(CURRENCY_CODE)
            .build())
        .build();
  }

  private static Deposits mockZeroAmountPayment() {
    return Deposits.builder()
        .paymentReference(PAYMENT_REFERENCE)
        .postedAmount(CurrencyAmountType.builder()
            .amount(BigDecimal.ZERO.stripTrailingZeros())
            .currencyCode(CURRENCY_CODE)
            .build())
        .build();
  }

  private static Basket mockBasket() {
    return Basket.builder()
        .basketId(BASKET_ID)
        .reference(BASKET_REF)
        .hotelId(HOTEL_ID)
        .item(BasketItem.builder().sourceId(RESERVATION_ID).build())
        .build();
  }
}