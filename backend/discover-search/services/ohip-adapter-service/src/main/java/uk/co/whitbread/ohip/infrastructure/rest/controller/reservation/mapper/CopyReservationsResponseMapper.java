package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.out.CopyReservationsResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CopyReservationsResponseDto;

@Mapper(componentModel = "spring")
public interface CopyReservationsResponseMapper {

  CopyReservationsResponseDto toDto(CopyReservationsResponse copyReservationsResponse);
}
