package uk.co.whitbread.basket.processor.infrastructure.queue;

import static uk.co.whitbread.basket.processor.domain.logic.BasketOrderProcessInPortImpl.CARD_TYPE;
import static uk.co.whitbread.basket.processor.domain.logic.BasketOrderProcessInPortImpl.PAYMENT_METHOD;
import static uk.co.whitbread.basket.processor.domain.logic.BasketOrderProcessInPortImpl.PAYMENT_OPTION;
import static uk.co.whitbread.basket.processor.domain.logic.BasketOrderProcessInPortImpl.PAYMENT_TYPE;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.processor.domain.ports.primary.BasketOrderProcessInPort;
import uk.co.whitbread.basket.processor.infrastructure.queue.mapper.BasketOrderMapper;
import uk.co.whitbread.basket.processor.infrastructure.queue.model.order.BasketOrderEvent;
import uk.co.whitbread.basket.processor.infrastructure.queue.model.order.BasketRequestAction;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.exception.AmendReservationException;

@Component
@Slf4j
@RequiredArgsConstructor
public class BasketOrderConsumer {

  private final BasketOrderProcessInPort basketOrderProcessInPort;
  private final BasketOrderMapper basketOrderMapper;
  public static final String REQ_ACTION = "reqAction";

  @RetryableTopic(
      kafkaTemplate = "dlKafkaTemplate",
      attempts = "5",
      backOff = @BackOff(delay = 500, multiplier = 2),
      autoCreateTopics = "false",
      topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
      exclude = AmendReservationException.class)
  @KafkaListener(
          topics = {"${basket.topics.orders.name}"},
          containerFactory = "basketOrderFactory",
          concurrency = "${basket.topics.orders.concurrency}")
  public void onOrder(BasketOrderEvent consumerRecord) {
    var recordData = consumerRecord.getData();
    log.info(
        "Order record: ref={}, paymentOption={}, paymentMethod={}, paymentType={}, cardType={}",
        consumerRecord.getBasketReference(), recordData.get(PAYMENT_OPTION),
        recordData.get(PAYMENT_METHOD), recordData.get(PAYMENT_TYPE), recordData.get(CARD_TYPE));
    switch (BasketRequestAction.valueOf(recordData.get(REQ_ACTION))) {
      case CANCEL -> basketOrderProcessInPort
              .processCancelOrder(basketOrderMapper.toDomain(consumerRecord));
      case COMMIT, CHANGE_PAY -> basketOrderProcessInPort
              .processOrder(basketOrderMapper.toDomain(consumerRecord));
      case AMEND -> basketOrderProcessInPort
              .processAmend(basketOrderMapper.toDomain(consumerRecord));
      default -> log.error("BasketOrderEvent cannot be processed {}", consumerRecord.getEventId());
    }
  }

  @DltHandler
  public void dltHandler(BasketOrderEvent failedEvent) {
    log.info("Failed to process event {}. Moving to Dead Letter Queue", failedEvent.getEventId());
    basketOrderProcessInPort.handleFailedOrder(basketOrderMapper.toDomain(failedEvent));
  }

}
