package uk.co.whitbread.basket.domain.logic;

import static uk.co.whitbread.basket.utils.SanitizingUtils.sanitize;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohPaymentRequest;
import uk.co.whitbread.basket.domain.model.ccuieckoh.out.CcuiPaymentStatusResponse;
import uk.co.whitbread.basket.domain.model.ccuieckoh.out.PaymentStatus;
import uk.co.whitbread.basket.domain.model.payments.out.EckohResponse;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.ports.primary.CcuiEckohInPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.CcuiEckohOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.PaymentOutPort;

@Slf4j
@RequiredArgsConstructor
public class CcuiEckohInPortImpl implements CcuiEckohInPort {

  private final BasketOutPort basketOutPort;
  private final PaymentOutPort paymentOutPort;
  private final CcuiEckohOutPort ccuiEckohOutPort;
  private final HotelReservationOutPort reservationOutPort;

  @Override
  public PaymentResponse getEckohPayment(String basketReference,
      EckohPaymentRequest eckohPaymentRequest) {
    log.debug("Entering get Eckoh iframe with reference={}", sanitize(basketReference));

    final var basket = basketOutPort.getBasketById(basketReference);
    final var reservations = reservationOutPort.getReservationsByBasketReference(basketReference,
        "false", false);

    final var paymentRequest = ccuiEckohOutPort.createPaymentRequest(eckohPaymentRequest);
    if (!reservations.getReservationByIdList().isEmpty()) {
      paymentRequest.getBooking().setArrivalDate(
          reservations.getReservationByIdList().get(0).getRoomStay().getArrivalDate());
      paymentRequest.getBooking().setDepartureDate(
          reservations.getReservationByIdList().get(0).getRoomStay().getDepartureDate());
    }
    final var paymentResponse = paymentOutPort.createPayment(paymentRequest);

    basket.setPaymentID(paymentResponse.getPaymentId());
    basket.setStatus(BasketStatus.PAY_PENDING);
    basketOutPort.updateBasket(basket);
    return paymentResponse;
  }

  public CcuiPaymentStatusResponse paymentStatus(String basketReference) {
    log.debug("Entering payment status with reference={}", sanitize(basketReference));
    final var basket = basketOutPort.getBasketById(basketReference);
    final var paymentId = basket.getPaymentID();
    PaymentStatus paymentStatus;
    switch (basket.getStatus()) {
      case COMPLETED -> paymentStatus = PaymentStatus.SUCCESS;
      case FAILED, CANCELLED -> paymentStatus = PaymentStatus.ABANDONED;
      case OPEN, PROCESSING, PAY_PENDING -> {
        if (paymentId == null) {
          paymentStatus = PaymentStatus.NOT_FOUND;
          break;
        }
        var paymentResponse = paymentOutPort.getPaymentConfirmation(paymentId);
        paymentStatus = validateEckohResponse(
            paymentResponse.getProviderResponse().getEckohResponse());
      }
      default -> paymentStatus = PaymentStatus.NOT_FOUND;
    }
    return CcuiPaymentStatusResponse.builder()
        .status(paymentStatus)
        .build();
  }

  private PaymentStatus validateEckohResponse(EckohResponse eckohResponse) {
    log.debug("Entering validate eckoh response with eckohResponse={}", eckohResponse);
    if (eckohResponse == null) {
      return PaymentStatus.PENDING;
    }
    return eckohResponse.getResultCode() == 100 ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
  }
}