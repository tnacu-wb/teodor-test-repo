package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.TempBookingRefResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.TempBookingRefResponseDto;

@Mapper(componentModel = "spring")
public interface TempBookingRefResponseMapper {

  TempBookingRefResponseDto toDto(TempBookingRefResponse tempBookingRefResponse);
}
