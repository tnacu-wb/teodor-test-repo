package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.out.ConfirmReservationResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ConfirmReservationResponseDto;

@Mapper(componentModel = "spring")
public interface ConfirmReservationResponseMapper {

  ConfirmReservationResponseDto toConfirmReservationResponseDto(ConfirmReservationResponse confirmReservationResponse);
}
