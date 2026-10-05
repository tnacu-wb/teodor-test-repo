package uk.co.whitbread.refund.processor.infrastructure.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.stereotype.Component;
import uk.co.whitbread.refund.processor.domain.ports.primary.RefundRequestProcessInPort;
import uk.co.whitbread.refund.processor.infrastructure.queue.mapper.RefundRequestMapper;
import uk.co.whitbread.refund.processor.infrastructure.queue.model.refund.RefundRequestEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class RefundRequestConsumer {

  private final RefundRequestProcessInPort refundRequestProcessInPort;
  private final RefundRequestMapper refundRequestMapper;

  @RetryableTopic(
      kafkaTemplate = "dlKafkaTemplate",
      attempts = "5",
      backOff = @BackOff(delay = 500, multiplier = 2),
      autoCreateTopics = "false",
      topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
  @KafkaListener(
      topics = {"${rrp.topics.refunds.name}"},
      containerFactory = "refundRequestFactory",
      concurrency = "${rrp.topics.refunds.concurrency}")
  public void onRefund(ConsumerRecord<String, RefundRequestEvent> refundRequestEvent) {
    log.info("Processing refund for basket: {}", refundRequestEvent.value().getBasketReference());
    refundRequestProcessInPort.processRefund(refundRequestMapper.toDomain(refundRequestEvent.value()));
  }

  @DltHandler
  public void dltHandler(RefundRequestEvent failedEvent) {
    log.error("Failed to process event {}. Moving to Dead Letter Queue", failedEvent.getItemId());
    refundRequestProcessInPort.handleFailedRefund(refundRequestMapper.toDomain(failedEvent));
  }
}
