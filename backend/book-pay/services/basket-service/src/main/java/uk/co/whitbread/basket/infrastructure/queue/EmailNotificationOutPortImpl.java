package uk.co.whitbread.basket.infrastructure.queue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.email.out.EmailNotificationEventType;
import uk.co.whitbread.basket.domain.model.email.out.TransactionData;
import uk.co.whitbread.basket.domain.ports.secondary.EmailNotificationOutPort;
import uk.co.whitbread.basket.generated.models.reservation.ReservationByBasketRefRequestDto;
import uk.co.whitbread.basket.infrastructure.queue.exception.BasketOrderException;
import uk.co.whitbread.basket.infrastructure.queue.model.EmailItem;
import uk.co.whitbread.basket.infrastructure.queue.model.EmailNotificationEvent;
import uk.co.whitbread.basket.infrastructure.queue.producer.EmailNotificationProducer;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.service.ReservationClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@AllArgsConstructor
public class EmailNotificationOutPortImpl implements EmailNotificationOutPort {

  private final EmailNotificationProducer emailNotificationProducer;
  private final ReservationClient reservationClient;

  private static final String SOURCE_SYSTEM_ID = "OPERA";
  private static final String MAIL_EXCLUSION = "email";
  private static final String DISTRIBUTION_CHANNEL = "DISTR";
  public static final String GDS_AMADEUS_SUBCHANNEL = "AMADEUS";
  public static final String GDS_TRAVELPORT_SUBCHANNEL = "TRAVELPORT";

  @Override
  public void sendEmailNotificationEvent(final Basket basket,
      final EmailNotificationEventType event,
      final String emailAddress,
      final TransactionData transactionData,
      final List<String> mailSuppressionSorceCodes) {
    List<EmailItem> items = Optional.ofNullable(basket.getItems())
        .orElseThrow(() -> {
          var exception = new BasketOrderException(
              ErrorCode.DIGITAL_EMAIL_NOTIFICATION_EXCEPTION, "No items to process");
          ExceptionLogger.log(log, exception);
          return exception;
        })
        .stream()
        .map(item -> EmailItem.builder().sourceId(item.getSourceId()).type(item.getType())
            .sourceSystemId(SOURCE_SYSTEM_ID).build())
        .toList();

    Map<String, String> details = new HashMap<>();
    details.put("emailAddress", emailAddress);

    List<String> excludedPurposes = new ArrayList<>();

    if (DISTRIBUTION_CHANNEL.equals(basket.getChannel())) {

      var requestDto = new ReservationByBasketRefRequestDto();
      requestDto.setPriceBreakDownNeeded("false");
      requestDto.setRateInfoNeeded(false);
      var reservationByBasketRefOptional = reservationClient
          .getReservationsByBasketReference(basket.getBasketId(), requestDto)
          .getReservationByIdList()
          .stream()
          .findFirst();
      if (reservationByBasketRefOptional.isPresent()
          && (!reservationByBasketRefOptional.get().getReservationEmailNotifications().getSendEmailConfirmation()
          || mailSuppressionSorceCodes.contains(reservationByBasketRefOptional.get().getRoomStay().getSourceCode()))
          && !isGdsRequest(basket)) {
        excludedPurposes.add(MAIL_EXCLUSION);
      }
    } else {
      if (!basket.getSendMail()) {
        excludedPurposes.add(MAIL_EXCLUSION);
      }
    }

    var emailNotificationEvent = EmailNotificationEvent.builder()
        .id(UUID.randomUUID().toString())
        .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString())
        .bookingReference(basket.getReference())
        .hotelId(basket.getHotelId())
        .type(event.toString())
        .items(items)
        .transactionData(transactionData)
        .details(details)
        .excludedPurposes(excludedPurposes)
        .build();

    emailNotificationProducer.sendEmailNotificationEvent(emailNotificationEvent);
  }

  private boolean isGdsRequest(Basket basket) {
    return Objects.nonNull(basket.getSubChannel())
        && GDS_AMADEUS_SUBCHANNEL.equals(basket.getSubChannel())
        || GDS_TRAVELPORT_SUBCHANNEL.equals(basket.getSubChannel());
  }
}