package uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.dashboard.domain.model.in.ManageBookingResponse;
import uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.model.ManageBookingResponseDto;

@Mapper(componentModel = "spring")
public interface ManageBookingInfoMapper {

  ManageBookingResponse toModel(ManageBookingResponseDto manageBookingResponseDto);
}