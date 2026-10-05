package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.ConfirmReservationRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ConfirmReservationRequestDto;

@Mapper(componentModel = "spring")
public interface ConfirmReservationRequestMapper {

  ConfirmReservationRequest toModel(
      ConfirmReservationRequestDto confirmReservationRequestDto);
}
