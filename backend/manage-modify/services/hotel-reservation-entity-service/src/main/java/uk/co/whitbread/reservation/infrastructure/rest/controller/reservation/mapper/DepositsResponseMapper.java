package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.DepositsResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.DepositsResponseDto;

@Mapper(componentModel = "spring")
public interface DepositsResponseMapper {
  DepositsResponseDto toDto(DepositsResponse depositsResponse);
}