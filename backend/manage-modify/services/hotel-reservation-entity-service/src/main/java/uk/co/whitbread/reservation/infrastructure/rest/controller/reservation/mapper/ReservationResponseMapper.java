package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.ReservationProfiles;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.SaveReservationResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationProfilesDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ReservationsDetailsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.SaveReservationResponseDto;

@Mapper(componentModel = "spring", uses = {RoomStayMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ReservationResponseMapper {

  ReservationsDetailsResponseDto toDto(ReservationsDetailsResponse reservationsDetailsResponse);

  SaveReservationResponseDto toDto(SaveReservationResponse reservationResponse);

  ReservationProfilesDto toCreateProfileDto(ReservationProfiles resProfiles);
}
