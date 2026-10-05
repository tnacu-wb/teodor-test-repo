package uk.co.whitbread.ohip.infrastructure.rest.client.hotel.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelDetails;
import uk.co.whitbread.ohip.domain.model.hotel.out.HotelInfo;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HotelInfoMapper {

  @Mapping(target = "threeLetterId", source = "hotelConfigInfo.propertyControls.sellControls.hotelId")
  @Mapping(target = "hotelTimeZone", source = "hotelConfigInfo.propertyControls.dateTimeFormatting.timeZoneRegion")
  @Mapping(target = "hotelCountryCode", source = "hotelConfigInfo.address.country.code")
  @Mapping(target = "currencyCode", source = "hotelConfigInfo.propertyControls.currencyFormatting.currencyCode")
  @Mapping(target = "languageCode", source = "hotelConfigInfo.generalInformation.baseLanguage")
  @Mapping(target = "checkInTime", source = "hotelConfigInfo.generalInformation.checkInTime")
  @Mapping(target = "checkOutTime", source = "hotelConfigInfo.generalInformation.checkOutTime")
  HotelInfo toDomainModel(HotelDetails hotelDetails);
}
