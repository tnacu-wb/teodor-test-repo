package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.ReservationGuestResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationGuestResponseDto;

@Mapper(componentModel = "spring")
public interface ReservationGuestResponseMapper {

  ReservationGuestResponseDto toDto(ReservationGuestResponse reservationGuestResponse);

}
