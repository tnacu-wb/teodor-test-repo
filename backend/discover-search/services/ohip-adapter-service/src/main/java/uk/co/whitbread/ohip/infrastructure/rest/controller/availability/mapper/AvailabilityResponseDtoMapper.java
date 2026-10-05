package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityResult;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.AvailabilityResponseDto;

@Mapper(componentModel = "spring")
public interface AvailabilityResponseDtoMapper {

  AvailabilityResponseDto toDto(AvailabilityResult hotelAvailabilityResult);
}
