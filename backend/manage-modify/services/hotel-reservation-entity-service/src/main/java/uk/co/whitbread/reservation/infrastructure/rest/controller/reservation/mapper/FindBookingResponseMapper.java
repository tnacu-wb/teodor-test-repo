package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.FindBookingResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.FindBookingResponseDto;

@Mapper(componentModel = "spring", uses = {RoomStayMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FindBookingResponseMapper {
  FindBookingResponseDto toDto(FindBookingResponse reservationsResponse);

}
