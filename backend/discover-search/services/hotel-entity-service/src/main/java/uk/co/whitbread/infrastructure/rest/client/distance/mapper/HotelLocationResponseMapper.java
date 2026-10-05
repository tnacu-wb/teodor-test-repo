package uk.co.whitbread.infrastructure.rest.client.distance.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.domain.model.distance.out.DistanceFromSearchResponse;
import uk.co.whitbread.infrastructure.rest.client.distance.model.out.HotelLocationResponse;

@Mapper(componentModel = "spring")
public interface HotelLocationResponseMapper {

  @Mapping(target = "hotelId", source = "code")
  DistanceFromSearchResponse toModel(HotelLocationResponse hotelLocationResponse);

  @Mapping(target = "hotelId", source = "code")
  List<DistanceFromSearchResponse> toModels(List<HotelLocationResponse> hotelLocationResponseList);
}
