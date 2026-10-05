package uk.co.whitbread.basket.infrastructure.rest.controller.basket.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.basket.in.UpdateAllowancesRequest;
import uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in.UpdateAllowancesRequestDto;

@Mapper(componentModel = "spring")
public interface UpdateAllowancesRequestMapper {

  UpdateAllowancesRequest toDomainModel(UpdateAllowancesRequestDto updateAllowancesRequestDto);
}
