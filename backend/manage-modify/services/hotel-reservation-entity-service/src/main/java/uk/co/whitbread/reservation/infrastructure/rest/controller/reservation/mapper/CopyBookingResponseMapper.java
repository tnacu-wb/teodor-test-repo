package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.CopyBookingResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CopyBookingResponseDto;

@Mapper(componentModel = "spring")
public interface CopyBookingResponseMapper {

  CopyBookingResponseDto toDto(CopyBookingResponse reservationResponse);
}
