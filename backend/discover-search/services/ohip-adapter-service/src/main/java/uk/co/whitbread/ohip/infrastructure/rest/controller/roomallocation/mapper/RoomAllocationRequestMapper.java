package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.roomallocation.in.RoomAllocationRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.in.RoomAllocationRequestDto;

@Mapper(componentModel = "spring", uses = {
    CriteriaMapper.class
}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RoomAllocationRequestMapper {

  RoomAllocationRequest toAllocateRequestModel(RoomAllocationRequestDto allocateRequestDto);

}
