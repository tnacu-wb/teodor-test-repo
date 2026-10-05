package uk.co.whitbread.basket.infrastructure.queue.consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.infrastructure.queue.model.PaymentAuthorisedEvent;
import uk.co.whitbread.basket.infrastructure.queue.model.PaymentAuthorisedEventDTO;
import uk.co.whitbread.basket.infrastructure.queue.processor.PaymentAuthorisedEventProcessor;

@ExtendWith(MockitoExtension.class)
class PaymentAuthorisedEventConsumerTest {

  @Mock
  private PaymentAuthorisedEventProcessor paymentAuthorisedEventProcessor;

  @InjectMocks
  private PaymentAuthorisedEventConsumer paymentAuthorisedEventConsumer;

  @Test
  void shouldConsumeAndProcessPaymentAuthorisedEvent() {
    final var dto = PaymentAuthorisedEventDTO.builder()
        .basketId("AQN-147756bb-bb71-4842-959a-2efe87e378ed")
        .transactionId("190410112056083383")
        .paymentProvider("datatrans")
        .paymentMethod("VIS")
        .cardAlias("424242SKMPRI4242")
        .last4Digits("4242")
        .expiry("12/28")
        .authorizedAmount(8600)
        .currency("GBP")
        .build();
    final var consumerRecord = new ConsumerRecord<>("payment-authorised", 0, 1L, "key", dto);

    paymentAuthorisedEventConsumer.onPaymentAuthorised(consumerRecord);

    verify(paymentAuthorisedEventProcessor, times(1)).process(argThat(event ->
        "AQN-147756bb-bb71-4842-959a-2efe87e378ed".equals(event.getBasketReference())
            && "datatrans".equals(event.getPaymentProvider())
            && "190410112056083383".equals(event.getPaymentsConfirmation().getPaymentId())
            && "VIS".equals(event.getPaymentsConfirmation().getCardSchemeId())
            && "424242SKMPRI4242".equals(event.getPaymentsConfirmation().getToken())
            && "4242".equals(event.getPaymentsConfirmation().getLast4Digits())
            && "12/28".equals(event.getPaymentsConfirmation().getExpiry())
    ));
  }

  @Test
  void shouldPropagateProcessingFailure() {
    final var dto = PaymentAuthorisedEventDTO.builder()
        .basketId("basket-reference")
        .transactionId("tx-id")
        .paymentProvider("datatrans")
        .paymentMethod("VIS")
        .build();
    doThrow(new IllegalStateException("correlation failed"))
        .when(paymentAuthorisedEventProcessor)
        .process(argThat(e -> "basket-reference".equals(e.getBasketReference())));
    final var consumerRecord = new ConsumerRecord<>("payment-authorised", 0, 1L, "key", dto);

    assertThrows(IllegalStateException.class,
        () -> paymentAuthorisedEventConsumer.onPaymentAuthorised(consumerRecord));
  }

  @Test
  void shouldLogFailedEventOnDltHandler() {
    final var dto = PaymentAuthorisedEventDTO.builder()
        .basketId("basket-reference")
        .transactionId("tx-id")
        .build();
    final var failedRecord = new ConsumerRecord<>("payment-authorised-dlt", 0, 1L, "key", dto);

    paymentAuthorisedEventConsumer.dltHandler(failedRecord);
  }
}
