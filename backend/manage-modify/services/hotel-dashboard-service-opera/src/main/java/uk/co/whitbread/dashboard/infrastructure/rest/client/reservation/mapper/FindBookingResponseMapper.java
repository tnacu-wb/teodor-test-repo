package uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.dashboard.domain.model.in.FindBookingResponse;
import uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.model.FindBookingResponseDto;

@Mapper(componentModel = "spring")
public interface FindBookingResponseMapper {

  FindBookingResponse toModel(FindBookingResponseDto findBookingResponseDTO);
}