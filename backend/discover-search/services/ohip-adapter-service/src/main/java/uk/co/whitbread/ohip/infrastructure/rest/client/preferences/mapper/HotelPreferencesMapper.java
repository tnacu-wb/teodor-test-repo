package uk.co.whitbread.ohip.infrastructure.rest.client.preferences.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.preferences.HotelPreferencesOhipResponseDto;
import uk.co.whitbread.ohip.domain.model.preferences.out.HotelPreferencesResponse;

@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelPreferencesMapper {

  HotelPreferencesResponse toDomainModel(
      HotelPreferencesOhipResponseDto hotelPreferencesOhipResponseDtos);
}
