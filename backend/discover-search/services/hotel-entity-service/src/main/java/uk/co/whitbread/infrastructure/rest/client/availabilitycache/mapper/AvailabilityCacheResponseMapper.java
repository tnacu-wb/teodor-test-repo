package uk.co.whitbread.infrastructure.rest.client.availabilitycache.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.availabilitycache.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.infrastructure.rest.client.availabilitycache.model.out.HotelAvailabilitiesDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AvailabilityCacheResponseMapper {

  HotelAvailabilitiesResponse toModel(HotelAvailabilitiesDto hotelAvailabilitiesDto);

}
