package uk.co.whitbread.basket.infrastructure.queue.producer;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.infrastructure.queue.model.EmailNotificationEvent;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Slf4j
@Component
public class EmailNotificationProducer {

  private final KafkaTemplate<String, EmailNotificationEvent> kafkaTemplate;
  private final ConcurrentTracer tracer;

  public EmailNotificationProducer(
      @Qualifier("notifications") final KafkaTemplate<String, EmailNotificationEvent> kafkaTemplate,
      ConcurrentTracer tracer) {
    this.kafkaTemplate = kafkaTemplate;
    this.tracer = tracer;
  }

  public void sendEmailNotificationEvent(final EmailNotificationEvent emailNotificationEvent) {
    log.info("Sending email notification event: {}", emailNotificationEvent.getBookingReference());

    List<Header> headers = Arrays.asList(
        new RecordHeader("event.type", emailNotificationEvent.getType().getBytes()),
        new RecordHeader("event.id", emailNotificationEvent.getId().getBytes()));

    ProducerRecord<String, EmailNotificationEvent> emailNotificationRecord =
        new ProducerRecord<>(kafkaTemplate.getDefaultTopic(),
            null, emailNotificationEvent.getId(), emailNotificationEvent, headers);

    var completableFuture =
        kafkaTemplate.send(emailNotificationRecord);

    completableFuture.whenComplete(tracer.wrap(
        (result, ex) -> {
          if (ex != null) {
            log.error("Email notification {} could not be sent: {}", emailNotificationEvent.getId(), ex.getMessage(),
                ex);
          } else if (result != null) {
            log.info("Successfully sent email notification {}", emailNotificationEvent.getId());
          }
        }));
  }
}
