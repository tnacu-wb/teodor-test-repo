package uk.co.whitbread.basket.infrastructure.queue.producer;

import static java.time.Instant.now;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.CleanUpTime;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.out.BookingConfirmationDetails;
import uk.co.whitbread.basket.infrastructure.queue.model.BasketOrderEvent;
import uk.co.whitbread.basket.infrastructure.queue.processor.BasketOrderMapper;
import uk.co.whitbread.basket.infrastructure.repository.BasketRepository;
import uk.co.whitbread.basket.infrastructure.repository.mapper.BasketEntityMapper;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketStatusEntity;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@Component
@Slf4j
public class BasketOrderProducer {

  private static final String SUBCHANNEL_WEB = "WEB";
  private final KafkaTemplate<String, BasketOrderEvent> kafkaTemplate;
  private final BasketOrderMapper basketOrderMapper;
  private final BasketEntityMapper basketEntityMapper;
  private final BasketRepository basketRepository;
  private final ConcurrentTracer tracer;
  private final CleanUpTime cleanUpTime;

  public BasketOrderProducer(@Qualifier("orders") final KafkaTemplate<String, BasketOrderEvent> kafkaTemplate,
                             final BasketOrderMapper basketOrderMapper, BasketEntityMapper basketEntityMapper,
                             final BasketRepository basketRepository, final ConcurrentTracer tracer,
                             final CleanUpTime cleanUpTime) {
    this.kafkaTemplate = kafkaTemplate;
    this.basketOrderMapper = basketOrderMapper;
    this.basketEntityMapper = basketEntityMapper;
    this.basketRepository = basketRepository;
    this.tracer = tracer;
    this.cleanUpTime = cleanUpTime;
  }

  public void sendOrderAsync(final Basket basket, final String reqAction,
                             final BookingConfirmationDetails bookingConfirmationDetails,
                             final String ccAgentId) {
    final List<BasketOrderEvent> orders =
        basketOrderMapper.processBasket(basket, reqAction, bookingConfirmationDetails, ccAgentId);
    orders.forEach(this::send);
  }

  public void sendAmendAsync(final Basket basket, PaymentsConfirmation paymentsConfirmation,
                             final String reqAction) {
    final Map<String, String> data = Map.of(
        "reqAction", reqAction,
        "language", paymentsConfirmation.getLanguage().toUpperCase(),
        "channel", paymentsConfirmation.getChannel(),
        "subChannel", SUBCHANNEL_WEB,
        "tempBookingRef", basket.getBasketId(),
        "originalBookingRef", basket.getOriginalBasketId(),
        "paymentOptionSelected", paymentsConfirmation.getPaymentOptionSelected(),
        "emailAddress",
        !StringUtils.isEmpty(paymentsConfirmation.getEmailAddress()) ? paymentsConfirmation.getEmailAddress() : "",
        "ccAgentId",
        StringUtils.isEmpty(paymentsConfirmation.getCcAgentId()) ? "" : paymentsConfirmation.getCcAgentId(),
        "token", StringUtils.isEmpty(paymentsConfirmation.getToken()) ? "" : paymentsConfirmation.getToken()
    );

    final String id = String.join("#", "AMEND", basket.getReference());

    final BasketOrderEvent amendPayload = BasketOrderEvent
        .builder()
        .eventId(id)
        .basketReference(basket.getBasketId())
        .bookingReference(basket.getReference())
        .data(data)
        .build();

    send(amendPayload);
  }

  private void send(BasketOrderEvent basketOrderEvent) {
    log.info("Sending order basketEventId={}", basketOrderEvent.getEventId());

    var completableFuture =
        kafkaTemplate.send(kafkaTemplate.getDefaultTopic(), basketOrderEvent.getEventId(), basketOrderEvent);

    completableFuture.whenComplete(tracer.wrap(
        (result, ex) -> {
          if (ex != null) {
            log.error("Order basketEventOrder={} could not be sent  message={}",
                basketOrderEvent.getEventId(), ex.getMessage(), ex);
            basketRepository.getByReference(basketOrderEvent.getBasketReference())
                .ifPresentOrElse(basketEntity -> {
                  var basketStatusFailed = basketOrderEvent.getData().get("tempBookingRef") != null
                      ? BasketStatusEntity.AMEND_FAILED : BasketStatusEntity.FAILED;
                  basketEntity.setStatus(basketStatusFailed);
                  basketEntity.setCleanUpTime(
                      cleanUpTime.getCleanUpTime(
                          basketEntityMapper.toBasketStatusModel(basketStatusFailed), now()));
                  basketRepository.save(basketEntity);
                  String message = String.format(
                      "BasketEventOrder for basketReference = %s could not be sent and "
                          + "basket status has been set to %s",
                      basketEntity.getReference(), basketStatusFailed);
                  throwPaymentException(ErrorCode.DIGITAL_SENDING_ORDER_EXCEPTION, message);
                }, () -> {
                  String message = String.format(
                      "Order basketEventOrder=%s could not be updated to FAILED",
                      basketOrderEvent.getEventId());
                  throwPaymentException(ErrorCode.DIGITAL_SENDING_ORDER_INVALID_BASKET_EXCEPTION, message);
                });
          } else if (result != null) {
            log.info("Successfully sent order basketEventOrder={}", basketOrderEvent.getEventId());
          }
        }));
  }

  private void throwPaymentException(final ErrorCode errorCode, final String message) {
    var exception = new PaymentException(errorCode, message);
    ExceptionLogger.log(log, exception);
    throw exception;
  }
}
