package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.rules.agent.domain.model.in.OccupancySupplementRequest;
import uk.co.whitbread.rules.agent.domain.model.out.OccupancySupplementResponse;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.OccupancySupplementRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.OccupancySupplementResponseDto;

@Mapper(componentModel = "spring")
public interface OccupancySupplementDtoMapper {

  OccupancySupplementRequest toModel(OccupancySupplementRequestDto occupancySupplementRequestDto);

  OccupancySupplementResponseDto toDto(OccupancySupplementResponse occupancySupplementResponse);

}

