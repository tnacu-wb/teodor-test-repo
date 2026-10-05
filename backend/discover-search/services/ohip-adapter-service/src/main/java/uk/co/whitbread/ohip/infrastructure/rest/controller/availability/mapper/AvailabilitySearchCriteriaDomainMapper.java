package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilitySearchCriteria;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.AvailabilityRequestDto;

@Mapper(componentModel = "spring")
public interface AvailabilitySearchCriteriaDomainMapper {

  AvailabilitySearchCriteria toDomainModel(String hotelId,
      AvailabilityRequestDto hotelAvailabilityRequest);
}
