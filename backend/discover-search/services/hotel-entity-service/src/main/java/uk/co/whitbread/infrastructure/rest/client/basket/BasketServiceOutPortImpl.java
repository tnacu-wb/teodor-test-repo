package uk.co.whitbread.infrastructure.rest.client.basket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.domain.model.basket.out.Basket;
import uk.co.whitbread.domain.ports.secondary.BasketServiceOutPort;
import uk.co.whitbread.infrastructure.rest.client.BasketServiceClient;
import uk.co.whitbread.infrastructure.rest.client.basket.mapper.BasketMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class BasketServiceOutPortImpl implements BasketServiceOutPort {

  private final BasketServiceClient basketServiceClient;
  private final BasketMapper basketMapper;

  @Override
  public Basket getBasket(String basketReference) {
    var basketDto = basketServiceClient.getBasket(basketReference);
    return basketMapper.toModel(basketDto);
  }
}