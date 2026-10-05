package uk.co.whitbread.ohip.domain.ports.primary;

import uk.co.whitbread.ohip.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.ohip.domain.model.hotel.out.RoomTypesInfo;

public interface HotelInfoInPort {

  HotelInfo getHotelInfo(final String hotelId);

  RoomTypesInfo getRoomTypesInfo(final String hotelId);

}
