package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.BasketItemDto;


@Data
@Builder
@NoArgsConstructor
public class BasketItemInfoDto implements SelfValidation<BasketItemInfoDto> {

  private List<BasketItemDto> basketItems;
  private List<AddBasketItemTypeDto> basketItemTypes;

  public BasketItemInfoDto(List<BasketItemDto> basketItems,
                           List<AddBasketItemTypeDto> basketItemTypes) {
    this.basketItems = basketItems;
    this.basketItemTypes = basketItemTypes;
    this.validateSelf();
  }
}
