package uk.co.whitbread.basket.infrastructure.queue.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.infrastructure.queue.model.PaymentAuthorisedEvent;
import uk.co.whitbread.basket.infrastructure.queue.model.PaymentAuthorisedEventDTO;
import uk.co.whitbread.basket.infrastructure.queue.processor.PaymentAuthorisedEventProcessor;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentAuthorisedEventConsumer {

  private final PaymentAuthorisedEventProcessor paymentAuthorisedEventProcessor;

  @RetryableTopic(
      kafkaTemplate = "paymentsDlKafkaTemplate",
      attempts = "5",
      backOff = @BackOff(delay = 500, multiplier = 2),
      autoCreateTopics = "false",
      topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)
  @KafkaListener(
      topics = "${basket.topics.paymentAuthorised:payment-authorised}",
      containerFactory = "paymentsFactory",
      concurrency = "${basket.kafka.consumer.paymentAuthorised.concurrency:1}")
  public void onPaymentAuthorised(
      ConsumerRecord<String, PaymentAuthorisedEventDTO> consumerRecord) {
    log.info("Received payment-authorised record with key {}, value {}, offset {}, partition {} and timestamp {}",
        consumerRecord.key(), consumerRecord.value(), consumerRecord.offset(),
        consumerRecord.partition(), consumerRecord.timestamp());
    paymentAuthorisedEventProcessor.process(toPaymentAuthorisedEvent(consumerRecord.value()));
  }

  @DltHandler
  public void dltHandler(ConsumerRecord<String, PaymentAuthorisedEventDTO> failedRecord) {
    log.info("Failed to process payment-authorised event {}. Moving to Dead Letter Queue",
        failedRecord.value());
  }

  private PaymentAuthorisedEvent toPaymentAuthorisedEvent(PaymentAuthorisedEventDTO dto) {
    var paymentsConfirmation = PaymentsConfirmation.builder()
        .paymentId(dto.getTransactionId())
        .cardSchemeId(dto.getPaymentMethod())
        .last4Digits(dto.getLast4Digits())
        .paymentStatus(dto.getPaymentStatus())
        .language(dto.getLanguage())
        .expiry(dto.getExpiry())
        .token(dto.getCardAlias())
        .paymentOptionSelected(dto.getPaymentOption())
        .build();
    return PaymentAuthorisedEvent.builder()
        .basketReference(dto.getBasketId())
        .paymentProvider(dto.getPaymentProvider())
        .paymentsConfirmation(paymentsConfirmation)
        .build();
  }
}