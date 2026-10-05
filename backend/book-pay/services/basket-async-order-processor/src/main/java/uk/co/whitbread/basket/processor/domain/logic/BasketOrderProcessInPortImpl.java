package uk.co.whitbread.basket.processor.domain.logic;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.processor.domain.model.in.BasketOrder;
import uk.co.whitbread.basket.processor.domain.model.out.BookingChannel;
import uk.co.whitbread.basket.processor.domain.model.out.CancelReservationRequest;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmAmendRequest;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmReservationRequest;
import uk.co.whitbread.basket.processor.domain.model.out.PaymentCard;
import uk.co.whitbread.basket.processor.domain.model.out.PaymentOption;
import uk.co.whitbread.basket.processor.domain.ports.primary.BasketOrderProcessInPort;
import uk.co.whitbread.basket.processor.domain.ports.secondary.BasketAcknowledgeOutPort;
import uk.co.whitbread.basket.processor.domain.ports.secondary.BasketOrderProcessOutPort;

@Slf4j
public record BasketOrderProcessInPortImpl(
    BasketOrderProcessOutPort basketOrderProcessOutPort,
    BasketAcknowledgeOutPort basketAcknowledgeOutPort) implements BasketOrderProcessInPort {

  public static final String HOTEL_ID = "hotelId";
  public static final String SOURCE_ID = "sourceId";
  public static final String PAYMENT_OPTION = "paymentOption";
  public static final String PAYMENT_METHOD = "paymentMethod";
  public static final String TOKEN = "token";
  public static final String CARD_TYPE = "cardType";
  public static final String PAYMENT_TYPE = "paymentType";
  public static final String CARD_HOLDER_NAME = "cardHolderName";
  public static final String EXPIRATION_DATE = "expirationDate";
  public static final String CARD_NUMBER_LAST_4_DIGITS = "cardNumberLast4Digits";
  public static final String FAILED_STATUS = "FAILED";
  public static final String CANCELED_STATUS = "CANCELED_STATUS";
  public static final String AMEND_STATUS = "PRESENT";
  public static final String REQ_ACTION = "reqAction";
  public static final String PAYMENT_ID = "paymentID";
  public static final String CIT_ID = "citId";
  public static final String LANGUAGE = "language";
  public static final String CHANNEL = "channel";
  public static final String SUB_CHANNEL = "subChannel";
  public static final String TEMP_BOOKING_REF = "tempBookingRef";
  public static final String ORIGINAL_BOOKING_REF = "originalBookingRef";
  public static final String PAYMENT_OPTION_SELECTED = "paymentOptionSelected";
  public static final String EMAIL_ADDRESS = "emailAddress";
  public static final String CC_AGENT_ID = "ccAgentId";
  static final String THREE_DS_INDICATOR = "threeDSIndicator";

  @Override
  public void processOrder(final BasketOrder basketOrder) {
    Map<String, String> data = basketOrder.getData();
    final ConfirmReservationRequest confirmReservationRequest =
        ConfirmReservationRequest.builder()
            .hotelId(data.get(HOTEL_ID))
            .reservationId(data.get(SOURCE_ID))
            .paymentOption(PaymentOption.valueOf(data.get(PAYMENT_OPTION)))
            .paymentMethod(data.get(PAYMENT_METHOD))
            .paymentType(data.get(PAYMENT_TYPE))
            .paymentCard(isReserveWithoutCardOrAccount2Company(basketOrder)
                ? null : PaymentCard.builder()
                .token(data.get(TOKEN))
                .cardType(data.get(CARD_TYPE))
                .cardHolderName(data.get(CARD_HOLDER_NAME))
                .expirationDate(data.get(EXPIRATION_DATE))
                .cardNumberLast4Digits(data.get(CARD_NUMBER_LAST_4_DIGITS))
                .citId(data.get(CIT_ID))
                .build())
            .paymentId(isReserveWithoutCardOrAccount2Company(basketOrder)
                ? null : data.get(PAYMENT_ID))
            .ccAgentId(data.get(CC_AGENT_ID))
                .threeDSIndicator((data.get(THREE_DS_INDICATOR)))
            .build();
    final var response = basketOrderProcessOutPort.confirmReservation(confirmReservationRequest);
    basketAcknowledgeOutPort.sendAcknowledgeMessage(basketOrder.getBasketReference(),
        data.get(SOURCE_ID), data.get(REQ_ACTION), response.getReservationStatus());
  }

  @Override
  public void processCancelOrder(final BasketOrder basketOrder) {
    final CancelReservationRequest cancelReservationRequest = CancelReservationRequest.builder()
        .hotelId(basketOrder.getData().get(HOTEL_ID))
        .basketReference(basketOrder.getBasketReference())
        .reservationIds(List.of(basketOrder.getData().get(SOURCE_ID)))
        .paymentOption(PaymentOption.valueOf(basketOrder.getData().get(PAYMENT_OPTION)))
        .build();
    final var response = basketOrderProcessOutPort.cancelReservation(cancelReservationRequest);
    final var reservationStatus = response.getBasketReference() == null ? FAILED_STATUS : CANCELED_STATUS;
    basketAcknowledgeOutPort.sendAcknowledgeMessage(basketOrder.getBasketReference(),
        basketOrder.getData().get(SOURCE_ID), basketOrder.getData().get(REQ_ACTION), reservationStatus);
  }

  @Override
  public void processAmend(final BasketOrder basketOrder) {
    var data = basketOrder.getData();
    var confirmAmendRequest = ConfirmAmendRequest
        .builder()
        .originalBookingRef(data.get(ORIGINAL_BOOKING_REF))
        .tempBookingRef(data.get(TEMP_BOOKING_REF))
        .token(data.get(TOKEN))
        .paymentOptionSelected(data.get(PAYMENT_OPTION_SELECTED))
        .emailAddress(data.get(EMAIL_ADDRESS))
        .bookingChannel(BookingChannel
            .builder()
            .channel(data.get(CHANNEL))
            .subchannel(data.get(SUB_CHANNEL))
            .language(data.get(LANGUAGE))
            .build())
        .ccAgentId(data.get(CC_AGENT_ID))
        .build();

    final var response = basketOrderProcessOutPort.confirmAmend(confirmAmendRequest);
    var status = response ? AMEND_STATUS : FAILED_STATUS;
    basketAcknowledgeOutPort.sendAcknowledgeMessage(basketOrder.getBasketReference(),
        data.get(ORIGINAL_BOOKING_REF), data.get(REQ_ACTION),
        status);
  }

  @Override
  public void handleFailedOrder(final BasketOrder basketOrder) {
    log.info("Handle failed order basketOrder reference ={}", basketOrder.getBasketReference());
    String eventId =
        basketOrder.getData().get(SOURCE_ID) != null ? basketOrder.getData().get(SOURCE_ID)
            : basketOrder.getData().get(ORIGINAL_BOOKING_REF);
    basketAcknowledgeOutPort.sendAcknowledgeMessage(basketOrder.getBasketReference(),
        eventId, basketOrder.getData().get(REQ_ACTION), FAILED_STATUS);
  }

  private boolean isReserveWithoutCardOrAccount2Company(BasketOrder basketOrder) {
    return Stream.of(PaymentOption.RESERVE_WITHOUT_CARD, PaymentOption.ACCOUNT_COMPANY)
        .anyMatch(
            paymentOption -> paymentOption.equals(PaymentOption.valueOf(basketOrder.getData().get(PAYMENT_OPTION))));
  }
}
