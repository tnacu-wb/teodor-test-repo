package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowancesResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.BookingAllowancesResponseDto;

@Mapper(componentModel = "spring")
public interface BookingAllowancesResponseMapper {

  BookingAllowancesResponseDto toDto(BookingAllowancesResponse bookingAllowancesResponse);
}