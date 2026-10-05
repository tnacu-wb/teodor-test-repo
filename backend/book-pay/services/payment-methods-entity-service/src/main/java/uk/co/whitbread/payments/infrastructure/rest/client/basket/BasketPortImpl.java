package uk.co.whitbread.payments.infrastructure.rest.client.basket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.payments.domain.model.out.Basket;
import uk.co.whitbread.payments.domain.ports.secondary.BasketPort;
import uk.co.whitbread.payments.infrastructure.rest.client.basket.model.out.BasketDto;
import uk.co.whitbread.payments.infrastructure.rest.client.basket.service.BasketClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasketPortImpl implements BasketPort {

  private final BasketClient basketClient;

  @Override
  public Basket getBasket(String basketReference) {
    return mapToBasket(basketClient.sendGetBasketByReference(basketReference));
  }

  private Basket mapToBasket(BasketDto basketDto) {
    if (basketDto == null) {
      return null;
    }
    String status = basketDto.getStatus() != null ? basketDto.getStatus().name() : null;

    return Basket.builder()
        .hotelId(basketDto.getHotelId())
        .channel(basketDto.getChannel())
        .idContext(basketDto.getIdContext())
        .basketStatus(status)
        .build();
  }
}
