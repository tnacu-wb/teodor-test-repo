package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPreferencesRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationPreferencesRequestDto;

@Mapper(componentModel = "spring")
public interface ReservationPreferencesRequestMapper {

  ReservationPreferencesRequest toModel(final ReservationPreferencesRequestDto reservationPreferencesRequestDto);
}
