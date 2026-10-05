package uk.co.whitbread.content.domain.logic.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInformation;
import uk.co.whitbread.content.domain.model.hotel.out.HotelInformationExtended;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface HotelInformationExtendedMapper {

  @Mapping(source = "hotelInfo.hotelTimeZone", target = "timeZone")
  HotelInformationExtended toDomainModel(HotelInformation hotelInformation,
      HotelInfo hotelInfo);

  HotelInformationExtended toDomainModel(HotelInformation hotelInformation);

}
