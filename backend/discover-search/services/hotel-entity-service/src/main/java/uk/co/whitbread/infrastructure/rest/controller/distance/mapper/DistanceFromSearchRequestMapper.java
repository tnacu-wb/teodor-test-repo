package uk.co.whitbread.infrastructure.rest.controller.distance.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.distance.in.DistanceFromSearchRequest;
import uk.co.whitbread.infrastructure.rest.controller.distance.model.in.DistanceFromSearchRequestDto;

@Mapper(componentModel = "spring")
public interface DistanceFromSearchRequestMapper {

  DistanceFromSearchRequest toModel(String hotelId,
      DistanceFromSearchRequestDto distanceFromSearchRequestDto);

}
