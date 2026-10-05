package uk.co.whitbread.refund.processor.infrastructure.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.refund.processor.domain.ports.secondary.BasketAcknowledgeOutPort;

@Component
@RequiredArgsConstructor
public class BasketAcknowledgeOutPortImpl implements BasketAcknowledgeOutPort {

  private final BasketAcknowledgeProducer basketAcknowledgeProducer;

  @Override
  public void sendAcknowledgeMessage(String basketReference, String itemId, final String reqAction,
      boolean isRefunded) {
    basketAcknowledgeProducer.sendAckAsync(basketReference, itemId, reqAction, isRefunded);
  }
}