package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.ConfirmReservationResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ConfirmReservationResponseDto;

@Mapper(componentModel = "spring")
public interface ConfirmReservationResponseMapper {

  ConfirmReservationResponseDto toDto(
      ConfirmReservationResponse confirmReservationResponse);

}
