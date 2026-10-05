package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancelReservationResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CancelReservationResponseDto;

@Mapper(componentModel = "spring")
public interface CancelReservationResponseMapper {

  CancelReservationResponseDto toDto(CancelReservationResponse cancelReservationResponse);

}
