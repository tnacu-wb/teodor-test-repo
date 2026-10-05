package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AttachReservationProfileRequestDto;
import uk.co.whitbread.reservation.domain.model.in.AttachReservationProfileRequest;

@Mapper(componentModel = "spring")
public interface AttachReservationProfileRequestOhipMapper {

  AttachReservationProfileRequestDto toDto(
      AttachReservationProfileRequest reservationProfileRequest);

}
