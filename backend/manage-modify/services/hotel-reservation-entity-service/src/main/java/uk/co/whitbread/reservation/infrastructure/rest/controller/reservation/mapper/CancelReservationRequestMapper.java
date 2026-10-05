package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.CancelReservationRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.CancelReservationRequestDto;

@Mapper(componentModel = "spring")
public interface CancelReservationRequestMapper {

  CancelReservationRequest toModel(CancelReservationRequestDto cancelReservationRequestDto);

}
