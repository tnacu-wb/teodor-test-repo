package uk.co.whitbread.basket.domain.logic;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.domain.exception.BasketItemException;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.model.payments.out.BasketPaymentStatus;
import uk.co.whitbread.basket.domain.model.payments.out.RefundResponse;
import uk.co.whitbread.basket.domain.model.refund.in.RefundReason;
import uk.co.whitbread.basket.domain.model.refund.in.RefundRequest;
import uk.co.whitbread.basket.domain.model.reservation.out.Deposits;
import uk.co.whitbread.basket.domain.ports.primary.RefundInPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RefundOutPort;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@AllArgsConstructor
public class RefundInPortImpl implements RefundInPort {

  private final BasketOutPort basketOutPort;
  private final RefundOutPort refundOutPort;
  private final HotelReservationOutPort hotelReservationOutPort;

  @Override
  public Optional<RefundResponse> refundPayment(final String basketReference, final RefundRequest refundRequest) {
    switch (refundRequest.getRefundType()) {
      case FULL -> {
        var basket = basketOutPort.getBasketById(basketReference);
        refundRequest.getBooking().setChannel(Optional.ofNullable(basket.getPaymentChannel()).orElse("WEB"));
        var basketItems = basket.getItems();
        if (basketItems.isEmpty()) {
          var exception = new BasketItemException(ErrorCode.DIGITAL_BASKET_ITEM_REFUND_EXCEPTION,
              String.format("Basket item is missing for basket = %s", basketReference));
          ExceptionLogger.log(log, exception);
          throw exception;
        }
        var paymentReferences = basketItems
                .stream()
                .map(basketItem ->
                        hotelReservationOutPort.getDepositsForReservationId(
                                basket.getHotelId(), basketItem.getSourceId()).getDeposits())
                .flatMap(Collection::stream)
                .filter(deposit -> deposit.getPostedAmount().getAmount().signum() > 0)
                .map(Deposits::getPaymentReference)
                .distinct()
                .toList();

        boolean noPaymentReferenceInOpera = paymentReferences
                .stream()
                .allMatch(Objects::isNull);
        if (noPaymentReferenceInOpera && basket.getPaymentID() == null && refundRequest.getRefund().getCard() != null) {
          refundOutPort.processRefund(basketReference, refundRequest, null);
        } else {
          paymentReferences
                  .stream()
                  .filter(Objects::nonNull)
                  .forEach(paymentReference ->
                          refundOutPort.processRefund(basketReference, refundRequest, paymentReference));
        }
        return Optional.empty();
      }
      case PARTIAL -> {

        var refundResponse = Optional.ofNullable(refundRequest.getRefund().getCard())
            .map(card -> refundOutPort.processTokenRefund(refundRequest))
            .orElseThrow(() -> {
              var exception = new PaymentException(ErrorCode.DIGITAL_PARTIAL_REFUND_EXCEPTION,
                  "Cannot execute partial refund without card details");
              ExceptionLogger.log(log, exception);
              return exception;
            });

        refundResponse.ifPresent(response -> {
          if (RefundReason.CANCEL.equals(refundRequest.getRefund().getReason()) && response.isRefunded()) {
            var basket = basketOutPort.getBasketById(basketReference);
            basket.setPaymentStatus(BasketPaymentStatus.REFUNDED);
            basketOutPort.updateBasket(basket);
          }
        });

        return refundResponse;
      }
      default -> {
        var message = String.format(
            "No refunds can be processed for basketReference=%s with type refundType=%s",
            basketReference,
            refundRequest.getRefundType());
        var exception = new PaymentException(ErrorCode.DIGITAL_NO_REFUND_EXCEPTION, message);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
  }

}
