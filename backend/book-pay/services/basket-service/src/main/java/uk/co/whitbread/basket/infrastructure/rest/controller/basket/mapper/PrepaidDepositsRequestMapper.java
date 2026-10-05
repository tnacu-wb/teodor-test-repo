package uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDepositsRequest;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.PrepaidDepositsRequestDto;

@Mapper(componentModel = "spring")
public interface PrepaidDepositsRequestMapper {

  PrepaidDepositsRequest toModel(PrepaidDepositsRequestDto prepaidDepositsRequestDto);
}
