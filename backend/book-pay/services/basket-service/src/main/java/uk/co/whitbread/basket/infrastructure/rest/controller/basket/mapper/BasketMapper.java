package uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.BasketDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.mapper.CcuiExtraItemsMapper;

@Mapper(componentModel = "spring", uses = {BasketItemTypesMapper.class,
    CcuiExtraItemsMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BasketMapper {

  @Mapping(source = "itemTypes", target = "itemTypes", qualifiedByName = "toDtoItemTypes")
  @Mapping(source = "basketId", target = "reference")
  @Mapping(source = "reference", target = "bookingReference")
  BasketDto toDto(Basket basket);
}
