package uk.co.whitbread.infrastructure.rest.client.distance.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.distance.in.DistanceFromSearchRequest;
import uk.co.whitbread.infrastructure.rest.client.distance.model.in.HotelLocationRequest;

@Mapper(componentModel = "spring")
public interface HotelLocationRequestMapper {

  HotelLocationRequest toDto(DistanceFromSearchRequest distanceFromSearchRequest);
}
