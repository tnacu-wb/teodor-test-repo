package uk.co.whitbread.basket.confirmation.processor.infrastructure.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.confirmation.processor.domain.ports.primary.BasketConfirmationProcessorInPort;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.queue.mapper.BasketAcknowledgeMapper;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.queue.model.in.BasketAcknowledgeEventDto;

@Component
@Slf4j
@RequiredArgsConstructor
public class BasketAcknowledgeConsumer {

  private final BasketConfirmationProcessorInPort basketConfirmationProcessorInPort;
  private final BasketAcknowledgeMapper basketAcknowledgeMapper;

  @RetryableTopic(
      kafkaTemplate = "dlKafkaTemplate",
      attempts = "5",
      backOff = @BackOff(delay = 500, multiplier = 2),
      autoCreateTopics = "false",
      topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
  @KafkaListener(topics = {"${basket.confirmation.topics.ack.name}"},
      containerFactory = "basketAcknowledgeFactory",
      concurrency = "${basket.confirmation.topics.ack.concurrency}")
  public void onAcknowledge(ConsumerRecord<String, BasketAcknowledgeEventDto> consumerRecord) {
    log.info("Acknowledge record with key {}, value {}, offset {}, partition {} and timestamp {}",
        consumerRecord.key(), consumerRecord.value(), consumerRecord.offset(),
        consumerRecord.partition(), consumerRecord.timestamp());
    basketConfirmationProcessorInPort.processAcknowledge(
        basketAcknowledgeMapper.toModel(consumerRecord.value()));
  }

  @DltHandler
  public void dltHandler(ConsumerRecord<String, BasketAcknowledgeEventDto> failedRecord) {
    log.info("Failed to process event {}. Moving to Dead Letter Queue", failedRecord.value());
  }

}
