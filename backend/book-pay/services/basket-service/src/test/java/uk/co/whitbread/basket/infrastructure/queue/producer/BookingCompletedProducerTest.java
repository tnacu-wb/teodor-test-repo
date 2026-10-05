package uk.co.whitbread.basket.infrastructure.queue.producer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micrometer.tracing.Tracer;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import uk.co.whitbread.basket.infrastructure.queue.model.BookingCompletedEvent;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@ExtendWith(MockitoExtension.class)
class BookingCompletedProducerTest {

  private static final String TOPIC = "booking-completed";
  private static final String BASKET_REFERENCE = "LONHOL-12345";

  @Mock
  private KafkaTemplate<String, BookingCompletedEvent> kafkaTemplate;

  @Spy
  private ConcurrentTracer concurrentTracer = new ConcurrentTracer(Tracer.NOOP);

  private BookingCompletedProducer underTest;

  @BeforeEach
  void setUp() {
    underTest = new BookingCompletedProducer(kafkaTemplate, concurrentTracer);
  }

  @Test
  void sendBookingCompletedEvent_whenKafkaSendSucceeds_shouldLogSuccess() {
    // Arrange
    var event = bookingCompletedEvent();
    CompletableFuture<SendResult<String, BookingCompletedEvent>> future = mock(CompletableFuture.class);

    when(kafkaTemplate.getDefaultTopic()).thenReturn(TOPIC);
    when(kafkaTemplate.send(TOPIC, BASKET_REFERENCE, event)).thenReturn(future);
    when(future.whenComplete(any())).thenAnswer(invocation -> {
      BiConsumer<SendResult<String, BookingCompletedEvent>, Throwable> callback =
          invocation.getArgument(0);
      callback.accept(mock(SendResult.class), null);
      return null;
    });

    // Act
    underTest.sendBookingCompletedEvent(event);

    // Assert
    verify(kafkaTemplate).send(TOPIC, BASKET_REFERENCE, event);
  }

  @Test
  void sendBookingCompletedEvent_whenKafkaSendFails_shouldLogError() {
    // Arrange
    var event = bookingCompletedEvent();
    CompletableFuture<SendResult<String, BookingCompletedEvent>> future = mock(CompletableFuture.class);

    when(kafkaTemplate.getDefaultTopic()).thenReturn(TOPIC);
    when(kafkaTemplate.send(TOPIC, BASKET_REFERENCE, event)).thenReturn(future);
    when(future.whenComplete(any())).thenAnswer(invocation -> {
      BiConsumer<SendResult<String, BookingCompletedEvent>, Throwable> callback =
          invocation.getArgument(0);
      callback.accept(null, new RuntimeException("Kafka broker unavailable"));
      return null;
    });

    // Act — no exception expected; error is logged internally
    underTest.sendBookingCompletedEvent(event);

    // Assert
    verify(kafkaTemplate).send(TOPIC, BASKET_REFERENCE, event);
  }

  @Test
  void sendBookingCompletedEvent_whenKafkaCallbackReceivesNullResultAndNullException_shouldNotLogSuccessOrError() {
    // Arrange
    var event = bookingCompletedEvent();
    CompletableFuture<SendResult<String, BookingCompletedEvent>> future = mock(CompletableFuture.class);

    when(kafkaTemplate.getDefaultTopic()).thenReturn(TOPIC);
    when(kafkaTemplate.send(TOPIC, BASKET_REFERENCE, event)).thenReturn(future);
    when(future.whenComplete(any())).thenAnswer(invocation -> {
      BiConsumer<SendResult<String, BookingCompletedEvent>, Throwable> callback =
          invocation.getArgument(0);
      callback.accept(null, null);
      return null;
    });

    // Act
    underTest.sendBookingCompletedEvent(event);

    // Assert
    verify(kafkaTemplate).send(TOPIC, BASKET_REFERENCE, event);
    verify(future).whenComplete(Mockito.any());
  }

  private BookingCompletedEvent bookingCompletedEvent() {
    return BookingCompletedEvent.builder()
        .basketReference(BASKET_REFERENCE)
        .status("COMPLETED")
        .build();
  }
}
