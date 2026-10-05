package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationInvoicesResponse;
import uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out.ReservationInvoicesResponseDto;

@Mapper(componentModel = "spring")
public interface ReservationInvoicesResponseDtoMapper {

  ReservationInvoicesResponseDto toDto(ReservationInvoicesResponse reservationInvoicesResponse);
}

