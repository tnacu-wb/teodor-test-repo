package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositsResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.DepositsResponseDto;

@Mapper(componentModel = "spring")
public interface DepositsResponseMapper {
  DepositsResponseDto toDto(DepositsResponse depositsResponse);
}