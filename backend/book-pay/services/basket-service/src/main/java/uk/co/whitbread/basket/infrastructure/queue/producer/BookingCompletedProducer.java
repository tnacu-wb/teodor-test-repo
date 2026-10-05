package uk.co.whitbread.basket.infrastructure.queue.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.infrastructure.queue.model.BookingCompletedEvent;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Component
@Slf4j
public class BookingCompletedProducer {

  private final KafkaTemplate<String, BookingCompletedEvent> kafkaTemplate;
  private final ConcurrentTracer tracer;

  public BookingCompletedProducer(
      @Qualifier("bookingCompleted") final KafkaTemplate<String, BookingCompletedEvent> kafkaTemplate,
      final ConcurrentTracer tracer) {
    this.kafkaTemplate = kafkaTemplate;
    this.tracer = tracer;
  }

  public void sendBookingCompletedEvent(final BookingCompletedEvent event) {
    log.info("Sending booking completed event for basketReference={}", event.getBasketReference());
    var completableFuture =
        kafkaTemplate.send(kafkaTemplate.getDefaultTopic(), event.getBasketReference(), event);

    completableFuture.whenComplete(tracer.wrap((result, ex) -> {
      if (ex != null) {
        log.error("Booking completed event for basketReference={} could not be sent: {}",
            event.getBasketReference(), ex.getMessage(), ex);
      } else if (result != null) {
        log.info("Successfully sent booking completed event for basketReference={}",
            event.getBasketReference());
      }
    }));
  }
}
