package uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.basket.in.AddBasketItem;
import uk.co.whitbread.basket.domain.model.basket.in.AddBasketItemRequest;
import uk.co.whitbread.basket.domain.model.basket.in.AddBasketItemType;
import uk.co.whitbread.basket.domain.model.basket.in.UpdateBasketItemSupplement;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.AddBasketItemDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.AddBasketItemRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.AddBasketItemTypeDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.UpdateBasketItemSupplementDto;

@Mapper(componentModel = "spring")
public interface AddBasketItemRequestMapper {

  AddBasketItemRequest toDomainModel(AddBasketItemRequestDto addBasketItemRequestDto);

  AddBasketItem toDomainModel(AddBasketItemDto addBasketItemDto);

  AddBasketItemType toDomainModel(AddBasketItemTypeDto addBasketItemTypeDto);

  UpdateBasketItemSupplement toDomainModel(UpdateBasketItemSupplementDto updateBasketItemSupplementDto);
}
