package uk.co.whitbread.payment.orchestrator.infrastructure.kafka;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentAuthorisedEvent;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.PaymentEventPublisherPort;

/**
 * Kafka adapter that publishes {@link PaymentAuthorisedEvent} messages to the
 * {@code payment-authorised} topic.
 *
 * <p>Uses the basket ID as the message key to guarantee ordering per basket.
 * Publication is synchronous so that failures propagate to the Temporal activity
 * and trigger retries. Logs only non-sensitive identifiers (basketId, transactionId)
 * — no card data is ever written to logs.
 */
@Slf4j
@Component
public class KafkaPaymentEventPublisher implements PaymentEventPublisherPort {

  private final KafkaTemplate<String, PaymentAuthorisedEvent> kafkaTemplate;
  private final String topic;

  public KafkaPaymentEventPublisher(
      KafkaTemplate<String, PaymentAuthorisedEvent> kafkaTemplate,
      @Value("${payment.events.topics.payment-authorised}") String topic) {
    this.kafkaTemplate = kafkaTemplate;
    this.topic = topic;
  }

  @Override
  public void publish(PaymentAuthorisedEvent event) {
    try {
      CompletableFuture<SendResult<String, PaymentAuthorisedEvent>> future =
          kafkaTemplate.send(topic, event.basketId(), event);
      future.get(25, TimeUnit.SECONDS);
      log.info("Published PaymentAuthorisedEvent [basketId={}, transactionId={}]",
          event.basketId(), event.transactionId());
    } catch (ExecutionException e) {
      log.error("Failed to publish PaymentAuthorisedEvent [basketId={}, transactionId={}]",
          event.basketId(), event.transactionId(), e.getCause());
      throw new RuntimeException("Kafka publication failed", e.getCause());
    } catch (TimeoutException e) {
      log.error("Timed out publishing PaymentAuthorisedEvent [basketId={}, transactionId={}]",
          event.basketId(), event.transactionId(), e);
      throw new RuntimeException("Kafka publication timed out", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Interrupted while publishing PaymentAuthorisedEvent "
          + "[basketId={}, transactionId={}]",
          event.basketId(), event.transactionId(), e);
      throw new RuntimeException("Kafka publication interrupted", e);
    }
  }
}
