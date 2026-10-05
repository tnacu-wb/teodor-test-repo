package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.ReservationResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationResponseDto;

@Mapper(componentModel = "spring", uses = {CreateReservationResponseRoomStayMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CreateReservationResponseMapper {

  ReservationResponseDto toDto(ReservationResponse reservationResponse);

}
