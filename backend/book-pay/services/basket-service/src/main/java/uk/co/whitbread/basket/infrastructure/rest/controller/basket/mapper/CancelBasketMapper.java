package uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.basket.in.CancelBasketRequest;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.CancelBasketDto;

@Mapper(componentModel = "spring")
public interface CancelBasketMapper {
  CancelBasketRequest toDomainModel(String basketReference, CancelBasketDto cancelBasketDto);
}
