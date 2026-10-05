package uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.digitalkey.domain.model.roomallocation.out.AllocationResponse;
import uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out.AllocationResponseDto;

@Mapper(componentModel = "spring")
public interface AllocationResponseMapper {

  AllocationResponseDto toDto(AllocationResponse allocationResponse);

}
