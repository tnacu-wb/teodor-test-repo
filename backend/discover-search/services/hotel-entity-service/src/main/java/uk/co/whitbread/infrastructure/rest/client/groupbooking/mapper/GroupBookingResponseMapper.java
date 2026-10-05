package uk.co.whitbread.infrastructure.rest.client.groupbooking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.domain.model.groupbooking.out.GroupBookingResponse;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.model.out.GroupBookingResponseDynamicsDto;

@Mapper(componentModel = "spring")
public interface GroupBookingResponseMapper {

  @Mapping(target = "ticketNumber", source = "ticketnumber")
  @Mapping(target = "incidentId", source = "incidentid")
  GroupBookingResponse toModel(GroupBookingResponseDynamicsDto groupBookingResponseDto);

}
