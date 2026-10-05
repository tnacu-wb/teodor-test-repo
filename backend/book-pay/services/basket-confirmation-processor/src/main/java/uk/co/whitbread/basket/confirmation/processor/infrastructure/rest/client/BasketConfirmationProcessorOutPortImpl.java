package uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.confirmation.processor.domain.model.in.BasketAcknowledge;
import uk.co.whitbread.basket.confirmation.processor.domain.ports.secondary.BasketConfirmationProcessorOutPort;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.basket.BasketClient;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.mapper.ConfirmItemProcessingRequestBasketMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class BasketConfirmationProcessorOutPortImpl implements BasketConfirmationProcessorOutPort {

  private final BasketClient basketClient;
  private final ConfirmItemProcessingRequestBasketMapper confirmItemProcessingRequestBasketMapper;

  @Override
  public void sendAcknowledge(final BasketAcknowledge basketAcknowledge) {
    log.debug("Entering send acknowledge");
    basketClient.sendAcknowledge(basketAcknowledge.getItemId(),
        basketAcknowledge.getBasketReference(),
        confirmItemProcessingRequestBasketMapper.toDto(basketAcknowledge));
  }

}
