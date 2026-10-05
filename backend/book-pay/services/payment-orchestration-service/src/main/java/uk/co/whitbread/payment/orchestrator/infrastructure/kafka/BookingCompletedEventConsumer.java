package uk.co.whitbread.payment.orchestrator.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import uk.co.whitbread.payment.orchestrator.domain.model.BookingCompletedEvent;
import uk.co.whitbread.payment.orchestrator.infrastructure.temporal.PaymentWorkflowSignaler;

/**
 * Kafka consumer that listens to the {@code booking-completed} topic and signals
 * the corresponding Temporal payment workflow.
 *
 * <p>Messages are consumed as raw strings so that deserialization failures can be
 * handled gracefully without blocking the consumer. On successful parsing, the event
 * is delegated to the {@link PaymentWorkflowSignaler} which signals the appropriate
 * payment workflow (Secure Fields or Mobile SDK).
 *
 * <p>Error handling strategy:
 * <ul>
 *   <li>Malformed JSON — logged at ERROR level and skipped (no retry)</li>
 *   <li>Missing/null fields — logged at ERROR level and skipped</li>
 *   <li>Workflow signaling errors — handled by {@link PaymentWorkflowSignaler}</li>
 * </ul>
 *
 * <p>No sensitive data (card numbers, CVV) is present in BookingCompletedEvent
 * messages, so the full payload is safe to log for troubleshooting.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!integration")
public class BookingCompletedEventConsumer {

  private final ObjectMapper objectMapper;
  private final PaymentWorkflowSignaler paymentWorkflowSignaler;

  /**
   * Handles messages from the {@code booking-completed} Kafka topic.
   *
   * <p>Deserializes the message value to a {@link BookingCompletedEvent}, validates
   * required fields, and delegates to the workflow signaler. Any deserialization or
   * validation error is logged and the message is skipped to avoid blocking the consumer.
   *
   * @param record the Kafka consumer record with a String value payload
   */
  @KafkaListener(
      topics = "${payment.events.topics.booking-completed}",
      groupId = "${spring.kafka.consumer.group-id}"
  )
  public void handleBookingCompleted(ConsumerRecord<String, String> record) {
    log.info("Received BookingCompletedEvent message [topic={}, partition={}, offset={}, key={}]",
        record.topic(), record.partition(), record.offset(), record.key());

    BookingCompletedEvent event = deserialize(record.value());
    if (event == null) {
      return;
    }

    if (event.basketReference() == null || event.basketReference().isBlank()) {
      log.error("BookingCompletedEvent missing basketReference [partition={}, offset={}]. Skipping.",
          record.partition(), record.offset());
      return;
    }

    if (event.status() == null || event.status().isBlank()) {
      log.warn("BookingCompletedEvent has null/blank status [basketReference={}, partition={}, offset={}]. "
          + "Signaling workflow — will default to cancellation.",
          event.basketReference(), record.partition(), record.offset());
    } else if (!event.isCompleted() && !"FAILED".equals(event.status())) {
      log.warn("BookingCompletedEvent has unrecognised status [basketReference={}, status={}]. "
          + "Signaling workflow — will default to cancellation.",
          event.basketReference(), event.status());
    }

    log.info("Processing BookingCompletedEvent [basketReference={}, status={}]",
        event.basketReference(), event.status());

    paymentWorkflowSignaler.signalBookingCompleted(event.basketReference(), event);
  }

  private BookingCompletedEvent deserialize(String payload) {
    try {
      return objectMapper.readValue(payload, BookingCompletedEvent.class);
    } catch (JacksonException e) {
      log.error("Failed to deserialize BookingCompletedEvent. Skipping malformed message. "
          + "Payload: {}", payload, e);
      return null;
    }
  }
}
