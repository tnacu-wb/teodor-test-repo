package uk.co.whitbread.payments.infrastructure.rest.client.hotels.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.payments.domain.model.out.HotelInfo;
import uk.co.whitbread.payments.infrastructure.rest.client.hotels.model.out.HotelInfoDto;

@Mapper(componentModel = "spring")
public interface HotelInfoMapper {
  HotelInfo toHotelInfoModel(HotelInfoDto hotelInfoDto);
}
