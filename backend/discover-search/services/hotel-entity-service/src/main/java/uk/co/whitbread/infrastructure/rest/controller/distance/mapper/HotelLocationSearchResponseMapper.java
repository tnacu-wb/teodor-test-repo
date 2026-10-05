package uk.co.whitbread.infrastructure.rest.controller.distance.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.distance.out.DistanceFromSearchResponse;
import uk.co.whitbread.infrastructure.rest.controller.distance.model.out.HotelLocationSearchResponseDto;

@Mapper(componentModel = "spring")
public interface HotelLocationSearchResponseMapper {

  HotelLocationSearchResponseDto toDto(
      DistanceFromSearchResponse distanceFromSearchResponse);

  List<HotelLocationSearchResponseDto> toDtos(
      List<DistanceFromSearchResponse> hotelDistances);
}
