package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.availability.out.MultiAvailabilityResult;
import uk.co.whitbread.ohip.domain.model.availability.out.MultiAvailabilityResultV2;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.MultiAvailabilityResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.MultiAvailabilityResponseV2Dto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MultiAvailabilityResponseDtoMapper {

  MultiAvailabilityResponseDto toResponseDto(MultiAvailabilityResult multiAvailabilityResult);

  MultiAvailabilityResponseV2Dto toV2ResponseDto(MultiAvailabilityResultV2 multiHotelAvailabilityResult);
}
