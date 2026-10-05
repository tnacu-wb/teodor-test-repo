package uk.co.whitbread.infrastructure.rest.client.hotel.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.domain.model.hotel.out.HotelPreferencesResponse;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelPreferencesResponseDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelInfoMapper {

  HotelInfo toDomainModel(HotelInfoDto hotelInfo);

  HotelPreferencesResponse toDomainModel(HotelPreferencesResponseDto hotelPreferencesResponseDto);

}
