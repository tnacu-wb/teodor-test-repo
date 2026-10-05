package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.CancelReservationResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CancelReservationResponseDto;

@Mapper(componentModel = "spring")
public interface CancelReservationResponseMapper {

  CancelReservationResponseDto toDto(CancelReservationResponse cancelReservationResponse);

}
