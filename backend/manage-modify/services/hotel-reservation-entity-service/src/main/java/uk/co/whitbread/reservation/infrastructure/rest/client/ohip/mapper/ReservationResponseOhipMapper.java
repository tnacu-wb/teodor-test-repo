package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CheckInResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationDetailsEnhancedDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationIdDetailsDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationResponseDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetailsResponseDto;
import uk.co.whitbread.reservation.domain.model.out.OhipReservationResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationIdDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsResponse;

@Mapper(componentModel = "spring", uses = {RoomStayOhipMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ReservationResponseOhipMapper {

  OhipReservationResponse toModel(ReservationResponseDto reservationResponseOhip);

  ReservationsDetailsResponse toModel(
      ReservationsDetailsResponseDto reservationsDetailsResponseOhip);

  @Mapping(target = "reservations", source = "reservationsDetailsResponse.reservations")
  ReservationsDetailsEnhancedResponse toModel(
      ReservationDetailsEnhancedDto reservationDetailsEnhancedDto);

  @Mapping(target = "reservationIdDetailsResponse", source = "reservationsDetailsResponse")
  ReservationByIdDetailsResponse toModel(ReservationIdDetailsDto ohipResponse);
}
