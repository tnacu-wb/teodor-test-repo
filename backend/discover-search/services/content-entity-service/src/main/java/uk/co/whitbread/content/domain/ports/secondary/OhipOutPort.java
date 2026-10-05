package uk.co.whitbread.content.domain.ports.secondary;

import uk.co.whitbread.content.domain.model.hotel.out.HotelInfo;

public interface OhipOutPort {

  HotelInfo getHotelInfo(String hotelId);

}
