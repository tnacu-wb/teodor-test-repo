package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.VacantRoomResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.VacantRoomResponseDto;

@Mapper(componentModel = "spring")
public interface VacantRoomResponseMapper {

  VacantRoomResponseDto toVacantRoomResponseDto(VacantRoomResponse vacantRoomResponse);

}
