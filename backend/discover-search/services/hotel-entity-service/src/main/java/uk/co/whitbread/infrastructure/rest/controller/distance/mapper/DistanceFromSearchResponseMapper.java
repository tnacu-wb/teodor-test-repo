package uk.co.whitbread.infrastructure.rest.controller.distance.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.distance.out.DistanceFromSearchResponse;
import uk.co.whitbread.infrastructure.rest.controller.distance.model.out.DistanceFromSearchResponseDto;

@Mapper(componentModel = "spring")
public interface DistanceFromSearchResponseMapper {

  DistanceFromSearchResponseDto toDto(DistanceFromSearchResponse distanceFromSearchResponse);
}
