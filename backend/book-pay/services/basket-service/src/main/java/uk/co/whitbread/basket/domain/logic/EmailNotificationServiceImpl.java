package uk.co.whitbread.basket.domain.logic;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import uk.co.whitbread.basket.domain.logic.config.DistributionProperties;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.email.out.EmailNotificationEventType;
import uk.co.whitbread.basket.domain.model.email.out.PaymentDetail;
import uk.co.whitbread.basket.domain.model.email.out.RefundDetail;
import uk.co.whitbread.basket.domain.model.email.out.TransactionData;
import uk.co.whitbread.basket.domain.model.feature.FeatureFlag;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.basket.domain.model.reservation.out.Deposits;
import uk.co.whitbread.basket.domain.ports.secondary.EmailNotificationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.PaymentOutPort;

@Slf4j
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {

  private static final String CARD_NO_MASK = "XXXXXXXXXXXX";

  private final HotelReservationOutPort reservationOutPort;
  private final EmailNotificationOutPort emailNotificationOutPort;
  private final PaymentOutPort paymentOutPort;
  private final DistributionProperties distributionProperties;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  @Async
  public void sendEmailNotificationEvent(final Basket basket, final EmailNotificationEventType event,
      final String emailAddress, final Boolean isTransactionDataRequired) {

    var transactionData = TransactionData.builder()
        .paymentDetails(Collections.emptyList())
        .refundPaymentDetails(Collections.emptyList())
        .build();
    if (Boolean.TRUE.equals(isTransactionDataRequired)) {
      transactionData = getTransactionData(basket, null);
    }
    emailNotificationOutPort.sendEmailNotificationEvent(basket, event, emailAddress, transactionData,
        distributionProperties.getMailSuppressionSorceCodes());
  }

  @Async
  public void sendEmailNotificationEvent(final Basket basket, final EmailNotificationEventType event,
      final String emailAddress, boolean failedRefund, List<Deposits> deposits) {

    emailNotificationOutPort.sendEmailNotificationEvent(basket, event, emailAddress,
        getTransactionData(basket, deposits, failedRefund), distributionProperties.getMailSuppressionSorceCodes());
  }

  private TransactionData getTransactionData(Basket basket, List<Deposits> deposits) {
    return getTransactionData(basket, deposits, false);
  }

  private TransactionData getTransactionData(Basket basket, List<Deposits> deposits, boolean failedRefund) {

    if (Objects.isNull(deposits)) {
      deposits = basket.getItems()
          .stream()
          .map(basketItem ->
              reservationOutPort.getDepositsForReservationId(
                  basket.getHotelId(), basketItem.getSourceId()).getDeposits())
          .filter(Objects::nonNull)
          .flatMap(Collection::stream)
          .toList();
    }

    var paymentDetails =  deposits.stream()
        .filter(deposit -> Objects.nonNull(deposit.getPostedAmount())
            && BigDecimal.ZERO.compareTo(deposit.getPostedAmount().getAmount()) < 0)
        .map(deposit -> getPaymentDetail(deposit))
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .toList();

    var refundDetails =  deposits.stream()
        .filter(deposit -> Objects.nonNull(deposit.getPostedAmount())
            && BigDecimal.ZERO.compareTo(deposit.getPostedAmount().getAmount()) > 0)
        .map(deposit -> this.getRefundDetail(deposit, failedRefund))
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .toList();

    return TransactionData.builder()
        .paymentDetails(paymentDetails)
        .refundPaymentDetails(refundDetails)
        .build();
  }

  private List<PaymentDetail> getPaymentDetail(Deposits deposit) {

    var paymentDetails = new ArrayList<PaymentDetail>();
    if (StringUtils.isNotBlank(deposit.getPaymentReference())) {
      var paymentId = extractPaymentIdFromPaymentReference(deposit.getPaymentReference());
      var paymentResponse = paymentOutPort.getPaymentConfirmation(paymentId);
      paymentDetails.add(PaymentDetail.builder()
              .paymentId(paymentId)
              .amount(deposit.getPostedAmount().getAmount())
              .currency(deposit.getPostedAmount().getCurrencyCode())
              .cardNo(CARD_NO_MASK.concat(paymentResponse.getProviderResponse().getThreecResponse().getLast4Digits()))
              .cardType(paymentResponse.getProviderResponse().getThreecResponse().getCardSchemeId())
              .transactionTimestamp(paymentResponse.getCreatedOn().toInstant()
                      .truncatedTo(ChronoUnit.SECONDS).toString())
              .build());
    }
    return paymentDetails;
  }
  /* 3DS Indicator is a status (|1) received from Planet and appended to paymentId
  // then saved as paymentReference in Opera. e.g: (324355224D|1)
  */

  private String extractPaymentIdFromPaymentReference(String paymentReference) {
    if (paymentReference != null && paymentReference.contains("|")) {
      return paymentReference.substring(0, paymentReference.indexOf("|"));
    }
    return paymentReference;
  }

  private List<RefundDetail> getRefundDetail(Deposits deposit, boolean failedRefund) {
    var refundDetails = new ArrayList<RefundDetail>();
    if (StringUtils.isNotBlank(deposit.getPaymentReference())) {
      var refundResponse = paymentOutPort.getPaymentConfirmation(deposit.getPaymentReference());
      refundDetails.add(RefundDetail.builder()
          .paymentId(deposit.getPaymentReference())
          .amount(deposit.getPostedAmount().getAmount())
          .currency(deposit.getPostedAmount().getCurrencyCode())
          .cardNo(CARD_NO_MASK.concat(refundResponse.getProviderResponse().getThreecResponse().getLast4Digits()))
          .cardType(refundResponse.getProviderResponse().getThreecResponse().getCardSchemeId())
          .transactionTimestamp(refundResponse.getCreatedOn().toInstant()
              .truncatedTo(ChronoUnit.SECONDS).toString())
          .isRefundSuccess(!failedRefund)
          .build());
    }
    return refundDetails;
  }
}