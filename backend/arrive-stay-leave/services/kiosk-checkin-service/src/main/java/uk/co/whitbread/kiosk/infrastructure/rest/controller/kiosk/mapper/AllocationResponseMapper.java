package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.kiosk.domain.model.roomallocation.out.AllocationResponse;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.out.AllocationResponseDto;

@Mapper(componentModel = "spring")
public interface AllocationResponseMapper {

  AllocationResponseDto toDto(AllocationResponse allocationResponse);

}
