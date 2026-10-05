package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.ManageBookingResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ManageBookingResponseDto;

@Mapper(componentModel = "spring")
public interface ManageBookingResponseMapper {

  ManageBookingResponseDto toDto(ManageBookingResponse manageBookingResponse);
}
