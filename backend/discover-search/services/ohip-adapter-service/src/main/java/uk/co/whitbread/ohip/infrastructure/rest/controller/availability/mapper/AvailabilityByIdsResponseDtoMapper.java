package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityByIdsResult;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityByIdsResultV2;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.AvailabilityByIdsResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out.AvailabilityByIdsResponseV2Dto;

@Mapper(componentModel = "spring")
public interface AvailabilityByIdsResponseDtoMapper {

  AvailabilityByIdsResponseDto toDto(AvailabilityByIdsResult availabilityByIdsResult);

  AvailabilityByIdsResponseV2Dto toV2Dto(AvailabilityByIdsResultV2 availabilityByIdsResult);
}
