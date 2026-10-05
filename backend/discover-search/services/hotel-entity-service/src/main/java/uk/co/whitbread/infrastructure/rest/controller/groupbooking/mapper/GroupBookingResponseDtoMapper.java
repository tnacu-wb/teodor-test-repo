package uk.co.whitbread.infrastructure.rest.controller.groupbooking.mapper;


import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.groupbooking.out.GroupBookingResponse;
import uk.co.whitbread.infrastructure.rest.controller.groupbooking.model.out.GroupBookingResponseDto;

@Mapper(componentModel = "spring")
public interface GroupBookingResponseDtoMapper {

  GroupBookingResponseDto toDto(GroupBookingResponse groupBookingResponse);

}
