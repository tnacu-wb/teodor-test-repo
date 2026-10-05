package uk.co.whitbread.wallet.domain.ports.secondary;

import uk.co.whitbread.wallet.domain.model.out.HotelInfo;

public interface ContentOutPort {
  
  HotelInfo getHotelInformation(String country, String language, String hotelId);
}
