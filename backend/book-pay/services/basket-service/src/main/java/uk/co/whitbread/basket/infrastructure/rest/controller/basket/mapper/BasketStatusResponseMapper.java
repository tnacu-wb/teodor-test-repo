package uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatusResponse;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out.BasketStatusResponseDto;

@Mapper(componentModel = "spring")
public interface BasketStatusResponseMapper {
  BasketStatusResponseDto toDto(BasketStatusResponse basketStatusResponse);
}
