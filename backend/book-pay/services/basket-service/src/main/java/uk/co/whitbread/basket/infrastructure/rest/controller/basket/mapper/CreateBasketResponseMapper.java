package uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.CreateBasketResponseDto;

@Mapper(componentModel = "spring")
public interface CreateBasketResponseMapper {

  @Mapping(source = "basketId", target = "reference")
  @Mapping(source = "reference", target = "bookingReference")
  CreateBasketResponseDto toDto(Basket basket);
}
