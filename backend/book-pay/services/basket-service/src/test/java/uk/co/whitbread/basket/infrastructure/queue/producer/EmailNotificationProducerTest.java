package uk.co.whitbread.basket.infrastructure.queue.producer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.tracing.Tracer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import uk.co.whitbread.basket.domain.model.email.out.EmailNotificationEventType;
import uk.co.whitbread.basket.infrastructure.queue.model.EmailItem;
import uk.co.whitbread.basket.infrastructure.queue.model.EmailNotificationEvent;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@ExtendWith(MockitoExtension.class)
class EmailNotificationProducerTest {

  @Mock
  private KafkaTemplate<String, EmailNotificationEvent> kafkaTemplate;

  @Spy
  private ConcurrentTracer concurrentTracer = new ConcurrentTracer(Tracer.NOOP);


  private EmailNotificationProducer emailNotificationProducer;

  @BeforeEach
  public void setUp() {
    emailNotificationProducer = new EmailNotificationProducer(kafkaTemplate, concurrentTracer);
  }

  @Test
  void testSendEmailNotificationEvent() {
    // Arrange
    when(kafkaTemplate.getDefaultTopic()).thenReturn("notifications");
    when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(mock(CompletableFuture.class));

    // Act
    emailNotificationProducer.sendEmailNotificationEvent(mockEmailNotificationEvent());

    // Assert
    verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
  }

  private EmailNotificationEvent mockEmailNotificationEvent() {
    Map<String, String> details = new HashMap<>();
    details.put("emailAddress", "test@gmail.com");

    List<EmailItem> items =
        Collections.singletonList(EmailItem.builder().sourceId("30000").type("STAY").sourceSystemId("OPERA").build());

    List<String> excludedPurposes = new ArrayList<>();

    return EmailNotificationEvent.builder()
        .id(UUID.randomUUID().toString())
        .bookingReference("REF12345")
        .type(EmailNotificationEventType.CONFIRM.toString())
        .createdAt(Instant.now().toString())
        .items(items)
        .details(details)
        .excludedPurposes(excludedPurposes)
        .build();
  }

}
