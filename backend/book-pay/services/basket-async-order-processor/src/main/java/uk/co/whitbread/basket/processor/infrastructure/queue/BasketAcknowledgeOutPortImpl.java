package uk.co.whitbread.basket.processor.infrastructure.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.processor.domain.ports.secondary.BasketAcknowledgeOutPort;

@Slf4j
@Component
@RequiredArgsConstructor
public class BasketAcknowledgeOutPortImpl implements BasketAcknowledgeOutPort {
  private final BasketAcknowledgeProducer basketAcknowledgeProducer;

  @Override
  public void sendAcknowledgeMessage(String basketReference, String itemId, String reqAction,
      String reservationStatus) {
    log.info("Entering send ack message for basketReference={}, itemId={}, reqAction={} and "
        + "reservationStatus={}", basketReference, itemId, reqAction, reservationStatus);
    basketAcknowledgeProducer.sendAckAsync(basketReference, itemId, reqAction, reservationStatus);
  }
}
