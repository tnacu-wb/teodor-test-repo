package uk.co.whitbread.infrastructure.rest.controller.groupbooking.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.groupbooking.in.GroupBookingRequest;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.model.in.GroupBookingRequestDto;

@Mapper(componentModel = "spring")
public interface GroupBookingRequestDtoMapper {

  GroupBookingRequest toModel(String hotelCode, GroupBookingRequestDto groupBookingRequestDto);
}
