package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfirmAmendOnReservationsRequestDto;
import uk.co.whitbread.reservation.domain.model.in.ConfirmAmendOnReservationsRequest;

@Mapper(componentModel = "spring")
public interface ConfirmAmendRequestOhipMapper {

  ConfirmAmendOnReservationsRequestDto toRequestDto(
      ConfirmAmendOnReservationsRequest confirmAmendOnReservationsRequest);

}
