package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.AttachReservationProfileRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.AttachReservationProfileRequestDto;

@Mapper(componentModel = "spring")
public interface AttachReservationProfileRequestMapper {

  AttachReservationProfileRequest toModel(AttachReservationProfileRequestDto attachReservationProfileRequestDto);

}
