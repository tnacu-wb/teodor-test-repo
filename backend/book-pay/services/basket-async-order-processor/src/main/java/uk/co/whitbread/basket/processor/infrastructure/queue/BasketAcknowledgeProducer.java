package uk.co.whitbread.basket.processor.infrastructure.queue;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.processor.domain.logic.BasketOrderProcessInPortImpl;
import uk.co.whitbread.basket.processor.infrastructure.queue.model.ack.ItemAcknowledgeEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class BasketAcknowledgeProducer {
  private final KafkaTemplate<String, ItemAcknowledgeEvent> ackKafkaTemplate;

  @Value("${config.service.reservation.status.reserved}")
  private String reservedStatus;

  public void sendAckAsync(final String basketReference, final String itemId,
                           final String reqAction, final String reservationStatus) {

    int status;
    if (reservedStatus.equalsIgnoreCase(reservationStatus) || reservationStatus.equals(
        BasketOrderProcessInPortImpl.CANCELED_STATUS) || reservationStatus.equals(
        BasketOrderProcessInPortImpl.AMEND_STATUS)) {
      status = 0;
    } else {
      log.error("Invalid reservation status:{} for booking:{}", reservationStatus, basketReference);
      status = 1;
    }

    Map<String, String> data = new HashMap<>();
    data.put("reqAction", reqAction);

    var itemAcknowledgeEventBuilder = ItemAcknowledgeEvent.builder()
        .basketReference(basketReference)
        .itemId(itemId)
        .status(status)
        .data(data);

    if (status == 1) {
      log.error("Reservation request returned status is={}", reservationStatus);
      itemAcknowledgeEventBuilder.error(
          "Reservation request status is: " + reservationStatus);
    }

    var future = ackKafkaTemplate.sendDefault(basketReference,
        itemAcknowledgeEventBuilder.build());

    future.whenComplete(
        (result, ex) -> {
          if (ex != null) {
            log.error("ACK could not be sent for basketRef: {}, item={}: {}", basketReference, itemId, ex.getMessage(),
                ex);
          } else if (result != null) {
            log.info("Successfully sent ACK for basketRef: {}, item: {}", basketReference, itemId);
          }
        });
  }
}
