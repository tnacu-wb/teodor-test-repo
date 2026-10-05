package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmAmendOnReservationsRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ConfirmAmendOnReservationsRequestDto;

@Mapper(componentModel = "spring")
public interface ConfirmAmendRequestMapper {

  ConfirmAmendOnReservationsRequest toRequestModel(
      ConfirmAmendOnReservationsRequestDto confirmAmendOnReservationsRequestDto);

}
