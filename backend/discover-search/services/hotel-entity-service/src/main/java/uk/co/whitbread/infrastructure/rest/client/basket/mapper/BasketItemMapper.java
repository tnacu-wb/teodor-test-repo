package uk.co.whitbread.infrastructure.rest.client.basket.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.generated.models.BasketItemDto;
import uk.co.whitbread.domain.model.basket.out.BasketItem;

@Mapper(componentModel = "spring")
public interface BasketItemMapper {

  BasketItem toModel(BasketItemDto basketItemDto);
}
