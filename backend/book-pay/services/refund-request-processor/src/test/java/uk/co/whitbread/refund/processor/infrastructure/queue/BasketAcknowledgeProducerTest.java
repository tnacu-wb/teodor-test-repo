package uk.co.whitbread.refund.processor.infrastructure.queue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.refund.processor.infrastructure.queue.BasketAcknowledgeProducer.FAILED_STATUS_MESSAGE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import uk.co.whitbread.refund.processor.infrastructure.queue.model.ack.ItemAcknowledgeEvent;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@ExtendWith(MockitoExtension.class)
class BasketAcknowledgeProducerTest {

  @Mock
  private KafkaTemplate<String, ItemAcknowledgeEvent> ackKafkaTemplate;

  @Mock
  private ConcurrentTracer tracer;

  private BasketAcknowledgeProducer basketAcknowledgeProducer;

  @BeforeEach
  void setUp() {
    basketAcknowledgeProducer = new BasketAcknowledgeProducer(ackKafkaTemplate, tracer);
    when(tracer.wrap(any(BiConsumer.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(ackKafkaTemplate.sendDefault(any(String.class), any(ItemAcknowledgeEvent.class)))
        .thenReturn(CompletableFuture.completedFuture(new SendResult<>(null, null)));
  }

  @Test
  void testSendAck_statusSuccess() {
    // Arrange
    var basketReference = "123";
    var itemId = "456";
    var paymentResponse = true;
    // Act
    basketAcknowledgeProducer.sendAckAsync(basketReference, itemId, "ROLLBACK", paymentResponse);

    // Assert
    var eventCaptor = ArgumentCaptor.forClass(ItemAcknowledgeEvent.class);
    verify(ackKafkaTemplate).sendDefault(eq("123"), eventCaptor.capture());
    ItemAcknowledgeEvent itemAcknowledgeEvent = eventCaptor.getValue();

    assertThat(itemAcknowledgeEvent.getItemId(), is("456"));
    assertThat(itemAcknowledgeEvent.getBasketReference(), is("123"));
    assertThat(itemAcknowledgeEvent.getStatus(), is(0));
  }

  @Test
  void testSendAck_statusFailed() {
    // Arrange
    var basketReference = "123";
    var itemId = "456";
    var paymentResponse = false;
    // Act
    basketAcknowledgeProducer.sendAckAsync(basketReference, itemId, "CANCEL", paymentResponse);

    // Assert
    var eventCaptor = ArgumentCaptor.forClass(ItemAcknowledgeEvent.class);
    verify(ackKafkaTemplate).sendDefault(eq("123"), eventCaptor.capture());
    ItemAcknowledgeEvent itemAcknowledgeEvent = eventCaptor.getValue();

    assertThat(itemAcknowledgeEvent.getItemId(), is("456"));
    assertThat(itemAcknowledgeEvent.getBasketReference(), is("123"));
    assertThat(itemAcknowledgeEvent.getStatus(), is(1));
    assertThat(itemAcknowledgeEvent.getErrors().get(0), is(FAILED_STATUS_MESSAGE));
  }
}
