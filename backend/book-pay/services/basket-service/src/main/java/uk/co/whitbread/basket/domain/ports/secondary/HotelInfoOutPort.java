package uk.co.whitbread.basket.domain.ports.secondary;

import uk.co.whitbread.basket.domain.model.hotel.out.HotelInfo;

public interface HotelInfoOutPort {

  HotelInfo getHotelInfo(final String hotelId);

}
