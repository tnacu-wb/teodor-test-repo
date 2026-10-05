package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.ReservationPreferencesRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationPreferencesRequestDto;

@Mapper(componentModel = "spring")
public interface ReservationPreferencesRequestMapper {

  ReservationPreferencesRequest toModel(
      ReservationPreferencesRequestDto reservationPreferencesRequestDto);
}
