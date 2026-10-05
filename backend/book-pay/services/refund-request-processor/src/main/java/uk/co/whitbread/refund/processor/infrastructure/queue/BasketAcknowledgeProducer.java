package uk.co.whitbread.refund.processor.infrastructure.queue;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uk.co.whitbread.refund.processor.infrastructure.queue.model.ack.ItemAcknowledgeEvent;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Component
@Slf4j
@RequiredArgsConstructor
public class BasketAcknowledgeProducer {

  private final KafkaTemplate<String, ItemAcknowledgeEvent> ackKafkaTemplate;
  private final ConcurrentTracer tracer;
  static final int SUCCESS_REFUND = 0;
  static final int FAILED_REFUND = 1;
  public static final String FAILED_STATUS_MESSAGE = "Refund request status is false";

  public void sendAckAsync(final String basketReference, final String itemId, final String reqAction,
                           final boolean isRefunded) {
    int status = isRefunded ? SUCCESS_REFUND : FAILED_REFUND;

    Map<String, String> data = new HashMap<>();
    data.put("reqAction", reqAction);

    var itemAcknowledgeEventBuilder = ItemAcknowledgeEvent.builder()
        .basketReference(basketReference)
        .itemId(itemId)
        .data(data)
        .status(status);

    if (status == FAILED_REFUND) {
      log.error("Refund request status is: {} with the basketReference: {}", status, basketReference);
      itemAcknowledgeEventBuilder.error(FAILED_STATUS_MESSAGE);
    }

    var future =
        ackKafkaTemplate.sendDefault(basketReference, itemAcknowledgeEventBuilder.build());
    future.whenComplete(tracer.wrap((result, throwable) -> {
      if (throwable != null) {
        log.warn("ACK could not be sent for basketRef: {}, item {}: {}", basketReference, itemId,
            throwable.getMessage(), throwable);
      } else if (result != null) {
        log.info("Successfully sent ACK for basketRef: {},  item: {} with offset {}", basketReference, itemId,
            result.getRecordMetadata().offset());
      }
    }));
  }
}
