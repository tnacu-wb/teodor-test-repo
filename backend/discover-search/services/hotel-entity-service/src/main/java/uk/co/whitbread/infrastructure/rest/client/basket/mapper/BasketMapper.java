package uk.co.whitbread.infrastructure.rest.client.basket.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.basket.generated.models.BasketDto;
import uk.co.whitbread.domain.model.basket.out.Basket;

@Mapper(componentModel = "spring", uses = {BasketItemMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BasketMapper {

  Basket toModel(BasketDto basketDto);
}