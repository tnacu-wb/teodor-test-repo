package uk.co.whitbread.ohip.domain.ports.secondary;

import uk.co.whitbread.ohip.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.ohip.domain.model.hotel.out.RoomTypesInfo;

public interface HotelInfoOutPort {

  HotelInfo getHotelInfo(final String hotelId);

  RoomTypesInfo getRoomTypesInfo(final String hotelId);

}
