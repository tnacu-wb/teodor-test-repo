package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPreferencesRequestDto;
import uk.co.whitbread.reservation.domain.model.in.ReservationPreferencesRequest;

@Mapper(componentModel = "spring")
public interface UpdatePreferencesRequestOhipMapper {

  ReservationPreferencesRequestDto toDto(ReservationPreferencesRequest reservationPreferencesRequest);
}
