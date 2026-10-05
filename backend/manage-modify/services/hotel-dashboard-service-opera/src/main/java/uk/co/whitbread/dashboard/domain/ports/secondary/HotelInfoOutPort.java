package uk.co.whitbread.dashboard.domain.ports.secondary;

import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.HotelInfo;

public interface HotelInfoOutPort {

  HotelInfo getHotelInfo(String hotelCode);
}
