package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationIdDetailsResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationIdDetailsDto;

@Mapper(componentModel = "spring")
public interface ReservationDetailsResponseMapper {

  @Mapping(source = "reservationIdResponse", target = "reservationsDetailsResponse")
  ReservationIdDetailsDto toDto(ReservationIdDetailsResponse reservationIdDetailsResponse);
}
