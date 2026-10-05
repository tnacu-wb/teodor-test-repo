package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfirmReservationRequestDto;
import uk.co.whitbread.reservation.domain.model.in.ConfirmReservationRequest;

@Mapper(componentModel = "spring")
public interface ConfirmReservationRequestOhipMapper {

  ConfirmReservationRequestDto toDto(ConfirmReservationRequest confirmReservationRequest);
}
