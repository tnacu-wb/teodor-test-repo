package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.RoomAllocationResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.RoomAllocationResponseDto;

@Mapper(componentModel = "spring")
public interface RoomAllocationResponseMapper {

  RoomAllocationResponseDto toDto(RoomAllocationResponse roomAllocationResponse);

}
