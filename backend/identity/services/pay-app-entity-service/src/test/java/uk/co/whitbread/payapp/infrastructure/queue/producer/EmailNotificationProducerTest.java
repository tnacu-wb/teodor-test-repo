package uk.co.whitbread.payapp.infrastructure.queue.producer;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.tracing.Tracer;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import uk.co.whitbread.payapp.infrastructure.queue.model.ShareAppEmailNotificationEvent;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@ExtendWith(MockitoExtension.class)
class EmailNotificationProducerTest {

  private EmailNotificationProducer emailNotificationProducer;

  @Mock
  private KafkaTemplate<String, ShareAppEmailNotificationEvent> kafkaTemplate;

  @Spy
  private ConcurrentTracer tracer = new ConcurrentTracer(Tracer.NOOP);

  @BeforeEach
  void setUp() {
    emailNotificationProducer = new EmailNotificationProducer(kafkaTemplate, tracer);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testSendShareAppEmailNotificationEvent_Success() {
    // Arrange
    ShareAppEmailNotificationEvent event = ShareAppEmailNotificationEvent.builder()
        .type("PAY_APP_SHARE")
        .id("12345")
        .build();

    CompletableFuture<RecordMetadata> future = CompletableFuture.completedFuture(
        new RecordMetadata(new TopicPartition("notifications", 0), 0, 0, 0L, 0, 0));
    when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);
    when(kafkaTemplate.getDefaultTopic()).thenReturn("notifications");

    // Act
    emailNotificationProducer.sendShareAppEmailNotificationEvent(event);

    // Assert
    ArgumentCaptor<ProducerRecord<String, ShareAppEmailNotificationEvent>> captor =
        ArgumentCaptor.forClass(ProducerRecord.class);
    verify(kafkaTemplate).send(captor.capture());

    ProducerRecord<String, ShareAppEmailNotificationEvent> capturedRecord = captor.getValue();
    assert capturedRecord != null;
    assert capturedRecord.value().equals(event);

    Header eventTypeHeader = capturedRecord.headers().lastHeader("event.type");
    assert eventTypeHeader != null;
    assert new String(eventTypeHeader.value()).equals("PAY_APP_SHARE");

    Header eventIdHeader = capturedRecord.headers().lastHeader("event.id");
    assert eventIdHeader != null;
    assert new String(eventIdHeader.value()).equals("12345");
  }

  @Test
  @SuppressWarnings("unchecked")
  void testSendShareAppEmailNotificationEvent_Failure() {
    // Arrange
    ShareAppEmailNotificationEvent event = ShareAppEmailNotificationEvent.builder()
        .type("PAY_APP_SHARE")
        .id("12345")
        .build();

    RuntimeException kafkaException = new RuntimeException("Kafka error");
    CompletableFuture<RecordMetadata> future = CompletableFuture.failedFuture(kafkaException);
    when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);
    when(kafkaTemplate.getDefaultTopic()).thenReturn("notifications");

    // Act
    emailNotificationProducer.sendShareAppEmailNotificationEvent(event);

    // Assert
    verify(kafkaTemplate).send(any(ProducerRecord.class));
    verify(tracer).wrap(Mockito.<BiConsumer<Object, RuntimeException>>argThat(callback -> {
      callback.accept(null, kafkaException);
      return true;
    }));
  }
}