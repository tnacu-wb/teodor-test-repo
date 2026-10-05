package uk.co.whitbread.basket.confirmation.processor.domain.logic;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.confirmation.processor.domain.model.in.BasketAcknowledge;
import uk.co.whitbread.basket.confirmation.processor.domain.ports.primary.BasketConfirmationProcessorInPort;
import uk.co.whitbread.basket.confirmation.processor.domain.ports.secondary.BasketConfirmationProcessorOutPort;

@Slf4j
public record BasketConfirmationProcessorInPortImpl(
    BasketConfirmationProcessorOutPort basketConfirmationProcessorOutPort) implements
    BasketConfirmationProcessorInPort {

  @Override
  public void processAcknowledge(final BasketAcknowledge basketAcknowledge) {
    basketConfirmationProcessorOutPort.sendAcknowledge(basketAcknowledge);
  }
}
