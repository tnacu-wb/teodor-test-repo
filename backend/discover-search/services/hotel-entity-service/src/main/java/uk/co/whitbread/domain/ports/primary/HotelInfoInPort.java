package uk.co.whitbread.domain.ports.primary;

import java.util.List;
import java.util.Map;
import uk.co.whitbread.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.domain.model.hotel.out.HotelPreferencesResponse;
import uk.co.whitbread.domain.model.hotel.out.RoomTypesInfo;

public interface HotelInfoInPort {

  HotelInfo getHotelInfo(final String hotelId);

  Map<String, RoomTypesInfo> getRoomTypesInfo(final List<String> hotelIds);

  HotelPreferencesResponse getHotelPreferences(String hotelId, String groupCode, String language);

}
