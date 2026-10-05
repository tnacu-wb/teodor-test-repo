package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.CdhSearchBookingsResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.CdhSearchBookingsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.CdhSearchBookingsResponseDto;

@Mapper(componentModel = "spring")
public interface CdhSearchBookingResponseMapper {

  CdhSearchBookingsResponseDto toDto(CdhSearchBookingsResponse cdhSearchBookingsResponse);
}
