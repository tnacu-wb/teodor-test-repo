package uk.co.whitbread.payment.orchestrator.infrastructure.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.payment.orchestrator.domain.model.BookingCompletedEvent;
import uk.co.whitbread.payment.orchestrator.infrastructure.temporal.PaymentWorkflowSignaler;

@ExtendWith(MockitoExtension.class)
class BookingCompletedEventConsumerTest {

  private static final String TOPIC = "booking-completed";
  private static final String BASKET_REFERENCE = "AQN-147756bb-bb71-4842-959a-2efe87e378ed";

  @Mock
  private PaymentWorkflowSignaler paymentWorkflowSignaler;

  private ObjectMapper objectMapper;
  private BookingCompletedEventConsumer underTest;

  private ListAppender<ILoggingEvent> logAppender;
  private Logger consumerLogger;

  @BeforeEach
  void setUp() {
    objectMapper = JsonMapper.builder().build();
    underTest = new BookingCompletedEventConsumer(objectMapper, paymentWorkflowSignaler);

    consumerLogger = (Logger) LoggerFactory.getLogger(BookingCompletedEventConsumer.class);
    logAppender = new ListAppender<>();
    logAppender.start();
    consumerLogger.addAppender(logAppender);
  }

  @AfterEach
  void tearDown() {
    consumerLogger.detachAppender(logAppender);
  }

  private ConsumerRecord<String, String> consumerRecord(String payload) {
    return new ConsumerRecord<>(TOPIC, 0, 42L, BASKET_REFERENCE, payload);
  }

  @Nested
  class SuccessfulProcessing {

    @Test
    void signalsWorkflowWithCompletedStatus() {
      String payload = """
          {"basketReference": "%s", "status": "COMPLETED"}
          """.formatted(BASKET_REFERENCE);

      underTest.handleBookingCompleted(consumerRecord(payload));

      ArgumentCaptor<BookingCompletedEvent> captor =
          ArgumentCaptor.forClass(BookingCompletedEvent.class);
      verify(paymentWorkflowSignaler).signalBookingCompleted(eq(BASKET_REFERENCE), captor.capture());

      BookingCompletedEvent captured = captor.getValue();
      assertThat(captured.basketReference()).isEqualTo(BASKET_REFERENCE);
      assertThat(captured.status()).isEqualTo("COMPLETED");
    }

    @Test
    void signalsWorkflowWithFailedStatus() {
      String payload = """
          {"basketReference": "%s", "status": "FAILED"}
          """.formatted(BASKET_REFERENCE);

      underTest.handleBookingCompleted(consumerRecord(payload));

      ArgumentCaptor<BookingCompletedEvent> captor =
          ArgumentCaptor.forClass(BookingCompletedEvent.class);
      verify(paymentWorkflowSignaler).signalBookingCompleted(eq(BASKET_REFERENCE), captor.capture());

      BookingCompletedEvent captured = captor.getValue();
      assertThat(captured.basketReference()).isEqualTo(BASKET_REFERENCE);
      assertThat(captured.status()).isEqualTo("FAILED");
    }

    @Test
    void logsEventDetailsOnSuccessfulProcessing() {
      String payload = """
          {"basketReference": "%s", "status": "COMPLETED"}
          """.formatted(BASKET_REFERENCE);

      underTest.handleBookingCompleted(consumerRecord(payload));

      assertThat(logAppender.list)
          .filteredOn(e -> e.getLevel() == Level.INFO)
          .anyMatch(e -> e.getFormattedMessage().contains(BASKET_REFERENCE)
              && e.getFormattedMessage().contains("COMPLETED"));
    }
  }

  @Nested
  class MalformedJson {

    @Test
    void skipsMessageAndDoesNotSignalWorkflow() {
      String payload = "not valid json {{{";

      assertThatNoException().isThrownBy(
          () -> underTest.handleBookingCompleted(consumerRecord(payload)));

      verify(paymentWorkflowSignaler, never()).signalBookingCompleted(any(), any());
    }

    @Test
    void logsErrorForMalformedPayload() {
      String payload = "not valid json {{{";

      underTest.handleBookingCompleted(consumerRecord(payload));

      assertThat(logAppender.list)
          .filteredOn(e -> e.getLevel() == Level.ERROR)
          .anyMatch(e -> e.getFormattedMessage().contains("Failed to deserialize"));
    }
  }

  @Nested
  class MissingOrBlankBasketReference {

    @Test
    void skipsMessageWhenBasketReferenceIsNull() {
      String payload = """
          {"basketReference": null, "status": "COMPLETED"}
          """;

      assertThatNoException().isThrownBy(
          () -> underTest.handleBookingCompleted(consumerRecord(payload)));

      verify(paymentWorkflowSignaler, never()).signalBookingCompleted(any(), any());
    }

    @Test
    void skipsMessageWhenBasketReferenceIsBlank() {
      String payload = """
          {"basketReference": "   ", "status": "COMPLETED"}
          """;

      assertThatNoException().isThrownBy(
          () -> underTest.handleBookingCompleted(consumerRecord(payload)));

      verify(paymentWorkflowSignaler, never()).signalBookingCompleted(any(), any());
    }

    @Test
    void skipsMessageWhenBasketReferenceIsMissing() {
      String payload = """
          {"status": "COMPLETED"}
          """;

      assertThatNoException().isThrownBy(
          () -> underTest.handleBookingCompleted(consumerRecord(payload)));

      verify(paymentWorkflowSignaler, never()).signalBookingCompleted(any(), any());
    }

    @Test
    void logsErrorWhenBasketReferenceIsInvalid() {
      String payload = """
          {"basketReference": "", "status": "COMPLETED"}
          """;

      underTest.handleBookingCompleted(consumerRecord(payload));

      assertThat(logAppender.list)
          .filteredOn(e -> e.getLevel() == Level.ERROR)
          .anyMatch(e -> e.getFormattedMessage().contains("missing basketReference"));
    }
  }
}
