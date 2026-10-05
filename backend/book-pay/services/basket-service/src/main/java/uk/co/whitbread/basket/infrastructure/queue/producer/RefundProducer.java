package uk.co.whitbread.basket.infrastructure.queue.producer;

import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.infrastructure.queue.model.RefundRequestEvent;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Component
@Slf4j
public class RefundProducer {

  private final KafkaTemplate<String, RefundRequestEvent> kafkaTemplate;
  private final ConcurrentTracer tracer;

  public RefundProducer(
      @Qualifier("refunds") final KafkaTemplate<String, RefundRequestEvent> kafkaTemplate,
      ConcurrentTracer tracer) {
    this.kafkaTemplate = kafkaTemplate;
    this.tracer = tracer;
  }

  public void sendRefundRequest(final RefundRequestEvent refundEvent) {
    log.info("Sending refund request event: {}", refundEvent);

    var completableFuture = kafkaTemplate.send(kafkaTemplate.getDefaultTopic(), refundEvent.getItemId(), refundEvent);

    completableFuture.whenComplete(tracer.wrap(
        (result, ex) -> {
          if (ex != null) {
            log.error("Refund Request {} could not be sent: {}", refundEvent.getItemId(), ex.getMessage(), ex);
          } else if (result != null) {
            log.info("Successfully sent refund request {}", refundEvent.getItemId());
          }
        }));
  }
}
