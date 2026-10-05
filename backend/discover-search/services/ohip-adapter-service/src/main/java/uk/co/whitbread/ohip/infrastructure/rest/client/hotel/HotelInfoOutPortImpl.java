package uk.co.whitbread.ohip.infrastructure.rest.client.hotel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.ohip.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.ohip.domain.model.hotel.out.RoomTypesInfo;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.OhipAvailabilityClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.hotel.mapper.HotelInfoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.hotel.mapper.RoomTypesInfoMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.hotel.ohip.OhipHotelConfigClient;

@RequiredArgsConstructor
@Slf4j
public class HotelInfoOutPortImpl implements HotelInfoOutPort {

  private final OhipHotelConfigClient ohipHotelConfigClient;
  private final OhipAvailabilityClient ohipAvailabilityClient;
  private final HotelInfoMapper hotelInfoMapper;
  private final RoomTypesInfoMapper roomTypesInfoMapper;

  @Override
  public HotelInfo getHotelInfo(String hotelId) {
    return hotelInfoMapper.toDomainModel(ohipHotelConfigClient.getHotelConfig(hotelId));
  }

  @Override
  public RoomTypesInfo getRoomTypesInfo(String hotelId) {
    var roomTypes = ohipAvailabilityClient.getRoomTypes(hotelId);

    if (roomTypes == null || CollectionUtils.isEmpty(roomTypes.getRoomTypesSummary())) {
      return RoomTypesInfo.builder().build();
    }

    return roomTypesInfoMapper.toDomainModel(roomTypes.getRoomTypesSummary().get(0));
  }

}
