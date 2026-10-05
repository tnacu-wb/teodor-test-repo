package uk.co.whitbread.payapp.infrastructure.queue.producer;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payapp.infrastructure.queue.model.ShareAppEmailNotificationEvent;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Slf4j
@Component
public class EmailNotificationProducer {

  private static final String EVENT_TYPE = "event.type";
  private static final String EVENT_ID = "event.id";
  private final KafkaTemplate<String, ShareAppEmailNotificationEvent> kafkaTemplate;
  private final ConcurrentTracer tracer;

  public EmailNotificationProducer(
      @Qualifier("notifications") final KafkaTemplate<String, ShareAppEmailNotificationEvent> kafkaTemplate,
      ConcurrentTracer tracer) {
    this.kafkaTemplate = kafkaTemplate;
    this.tracer = tracer;
  }

  public void sendShareAppEmailNotificationEvent(final ShareAppEmailNotificationEvent shareAppEmailNotificationEvent) {
    log.info("Sending PAY_APP_SHARE notification event: {}", shareAppEmailNotificationEvent);

    List<Header> headers = Arrays.asList(
        new RecordHeader(EVENT_TYPE, shareAppEmailNotificationEvent.getType().getBytes()),
        new RecordHeader(EVENT_ID, shareAppEmailNotificationEvent.getId().getBytes()));

    ProducerRecord<String, ShareAppEmailNotificationEvent> emailNotificationRecord =
        new ProducerRecord<>(kafkaTemplate.getDefaultTopic(),
            null, shareAppEmailNotificationEvent.getId(), shareAppEmailNotificationEvent, headers);

    var completableFuture = kafkaTemplate.send(emailNotificationRecord);

    completableFuture.whenComplete(tracer.wrap(
        (result, ex) -> {
          if (ex != null) {
            log.error("PAY_APP_SHARE email notification {} could not be sent: {}",
                shareAppEmailNotificationEvent.getId(), ex.getMessage(), ex);
          } else if (result != null) {
            log.info("Successfully sent PAY_APP_SHARE email notification {}",
                shareAppEmailNotificationEvent.getId());
          }
        }));
  }
}
