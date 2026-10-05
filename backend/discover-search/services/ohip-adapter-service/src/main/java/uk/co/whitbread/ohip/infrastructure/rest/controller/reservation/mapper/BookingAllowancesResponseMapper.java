package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.out.BookingAllowancesResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.BookingAllowancesResponseDto;

@Mapper(componentModel = "spring")
public interface BookingAllowancesResponseMapper {

  BookingAllowancesResponseDto toDto(BookingAllowancesResponse bookingAllowancesResponse);
}
