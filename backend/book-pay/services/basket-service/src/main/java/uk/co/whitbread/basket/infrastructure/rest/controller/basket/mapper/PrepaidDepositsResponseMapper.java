package uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDeposits;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.PrepaidDepositsDto;

@Mapper(componentModel = "spring")
public interface PrepaidDepositsResponseMapper {

  @Mapping(source = "prepaidDeposits", target = "prepaidDepositsDto")
  PrepaidDepositsDto toDto(PrepaidDeposits prepaidDeposits);
}
