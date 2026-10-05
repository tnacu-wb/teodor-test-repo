package uk.co.whitbread.infrastructure.rest.controller.srp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.infrastructure.rest.controller.srp.model.in.HotelAvailabilitiesRequestDto;

@Mapper(componentModel = "spring")
public interface HotelAvailabilitiesRequestDtoMapper {

  @Mapping(target = "pageSize", source = "hotelAvailabilitySearchRequest.initialPageSize")
  HotelAvailabilitiesRequest toModel(
      HotelAvailabilitiesRequestDto hotelAvailabilitySearchRequest);
}
