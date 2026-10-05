package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservationResponseDto;
import uk.co.whitbread.reservation.domain.model.out.CancelReservationResponse;

@Mapper(componentModel = "spring")
public interface CancelReservationResponseOhipMapper {

  CancelReservationResponse toModel(CancelReservationResponseDto cancelReservationResponseDto,
      String basketReference);
}
