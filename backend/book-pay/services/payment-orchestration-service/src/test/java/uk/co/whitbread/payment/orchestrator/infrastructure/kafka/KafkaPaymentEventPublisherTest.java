package uk.co.whitbread.payment.orchestrator.infrastructure.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentAuthorisedEvent;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentOption;

@ExtendWith(MockitoExtension.class)
class KafkaPaymentEventPublisherTest {

  private static final String TOPIC = "payment-authorised";
  private static final String BASKET_ID = "basket-123";
  private static final String TRANSACTION_ID = "txn-456";

  @Mock
  private KafkaTemplate<String, PaymentAuthorisedEvent> kafkaTemplate;

  private KafkaPaymentEventPublisher underTest;

  private ListAppender<ILoggingEvent> logAppender;
  private Logger publisherLogger;

  @BeforeEach
  void setUp() {
    underTest = new KafkaPaymentEventPublisher(kafkaTemplate, TOPIC);

    publisherLogger =
        (Logger) LoggerFactory.getLogger(KafkaPaymentEventPublisher.class);
    logAppender = new ListAppender<>();
    logAppender.start();
    publisherLogger.addAppender(logAppender);
  }

  @AfterEach
  void tearDown() {
    publisherLogger.detachAppender(logAppender);
  }

  private PaymentAuthorisedEvent sampleEvent() {
    return new PaymentAuthorisedEvent(
        BASKET_ID,
        TRANSACTION_ID,
        "datatrans",
        "VIS",
        "AAABcH0Bq92s3kgAESIAAbGj5NIs",
        "4242",
        "12/28",
        8600,
        "GBP",
        PaymentOption.PAY_NOW,
        "AUTHORIZED",
        "en"
    );
  }

  private CompletableFuture<SendResult<String, PaymentAuthorisedEvent>> completedFuture(
      PaymentAuthorisedEvent event) {
    RecordMetadata metadata = new RecordMetadata(
        new TopicPartition(TOPIC, 0), 0, 0, 0L, 0, 0);
    ProducerRecord<String, PaymentAuthorisedEvent> producerRecord =
        new ProducerRecord<>(TOPIC, BASKET_ID, event);
    return CompletableFuture.completedFuture(new SendResult<>(producerRecord, metadata));
  }

  private CompletableFuture<SendResult<String, PaymentAuthorisedEvent>> failedFuture() {
    CompletableFuture<SendResult<String, PaymentAuthorisedEvent>> future =
        new CompletableFuture<>();
    future.completeExceptionally(new RuntimeException("Broker unavailable"));
    return future;
  }

  @Nested
  class PublishSuccess {

    @Test
    void sendsToCorrectTopicWithBasketIdAsKey() {
      PaymentAuthorisedEvent event = sampleEvent();
      when(kafkaTemplate.send(eq(TOPIC), eq(BASKET_ID), any(PaymentAuthorisedEvent.class)))
          .thenReturn(completedFuture(event));

      underTest.publish(event);

      verify(kafkaTemplate).send(TOPIC, BASKET_ID, event);
    }

    @Test
    void logsInfoOnSuccessWithBasketIdAndTransactionId() {
      PaymentAuthorisedEvent event = sampleEvent();
      when(kafkaTemplate.send(eq(TOPIC), eq(BASKET_ID), any(PaymentAuthorisedEvent.class)))
          .thenReturn(completedFuture(event));

      underTest.publish(event);

      assertThat(logAppender.list)
          .filteredOn(e -> e.getLevel() == Level.INFO)
          .anyMatch(e -> e.getFormattedMessage().contains(BASKET_ID)
              && e.getFormattedMessage().contains(TRANSACTION_ID));
    }

    @Test
    void doesNotLogCardDataOnSuccess() {
      PaymentAuthorisedEvent event = sampleEvent();
      when(kafkaTemplate.send(eq(TOPIC), eq(BASKET_ID), any(PaymentAuthorisedEvent.class)))
          .thenReturn(completedFuture(event));

      underTest.publish(event);

      for (ILoggingEvent logEvent : logAppender.list) {
        String message = logEvent.getFormattedMessage();
        assertThat(message).doesNotContain("AAABcH0Bq92s3kgAESIAAbGj5NIs");
        assertThat(message).doesNotContain("4242");
        assertThat(message).doesNotContain("12/28");
      }
    }
  }

  @Nested
  class PublishFailure {

    @Test
    void throwsRuntimeExceptionOnSendFailure() {
      PaymentAuthorisedEvent event = sampleEvent();
      when(kafkaTemplate.send(eq(TOPIC), eq(BASKET_ID), any(PaymentAuthorisedEvent.class)))
          .thenReturn(failedFuture());

      assertThatThrownBy(() -> underTest.publish(event))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("Kafka publication failed");
    }

    @Test
    void logsErrorOnFailureWithBasketIdAndTransactionId() {
      PaymentAuthorisedEvent event = sampleEvent();
      when(kafkaTemplate.send(eq(TOPIC), eq(BASKET_ID), any(PaymentAuthorisedEvent.class)))
          .thenReturn(failedFuture());

      try {
        underTest.publish(event);
      } catch (RuntimeException _) {
        // expected
      }

      assertThat(logAppender.list)
          .filteredOn(e -> e.getLevel() == Level.ERROR)
          .anyMatch(e -> e.getFormattedMessage().contains(BASKET_ID)
              && e.getFormattedMessage().contains(TRANSACTION_ID));
    }

    @Test
    void doesNotLogCardDataOnFailure() {
      PaymentAuthorisedEvent event = sampleEvent();
      when(kafkaTemplate.send(eq(TOPIC), eq(BASKET_ID), any(PaymentAuthorisedEvent.class)))
          .thenReturn(failedFuture());

      try {
        underTest.publish(event);
      } catch (RuntimeException _) {
        // expected
      }

      for (ILoggingEvent logEvent : logAppender.list) {
        String message = logEvent.getFormattedMessage();
        assertThat(message).doesNotContain("AAABcH0Bq92s3kgAESIAAbGj5NIs");
        assertThat(message).doesNotContain("4242");
        assertThat(message).doesNotContain("12/28");
      }
    }

    @Test
    void throwsRuntimeExceptionOnTimeout() {
      PaymentAuthorisedEvent event = sampleEvent();
      CompletableFuture<SendResult<String, PaymentAuthorisedEvent>> future =
          new CompletableFuture<>();
      future.completeExceptionally(new TimeoutException("Timed out"));
      when(kafkaTemplate.send(eq(TOPIC), eq(BASKET_ID), any(PaymentAuthorisedEvent.class)))
          .thenReturn(future);

      assertThatThrownBy(() -> underTest.publish(event))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("Kafka publication failed");
    }

    @Test
    void throwsRuntimeExceptionOnInterrupt() {
      PaymentAuthorisedEvent event = sampleEvent();
      // Interrupt the thread to simulate InterruptedException from future.get()
      when(kafkaTemplate.send(eq(TOPIC), eq(BASKET_ID), any(PaymentAuthorisedEvent.class)))
          .thenAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return new CompletableFuture<>();
          });

      assertThatThrownBy(() -> underTest.publish(event))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("Kafka publication");

      // Clear interrupt flag for other tests
      Thread.interrupted();
    }
  }
}
