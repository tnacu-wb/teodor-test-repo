package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuestResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationGuestResponseDto;

@Mapper(componentModel = "spring")
public interface ReservationGuestResponseMapper {

  ReservationGuestResponseDto toDto(ReservationGuestResponse reservationGuestResponse);

}
