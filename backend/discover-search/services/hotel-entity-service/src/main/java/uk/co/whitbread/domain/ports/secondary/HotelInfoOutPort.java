package uk.co.whitbread.domain.ports.secondary;

import java.util.List;
import java.util.Map;
import uk.co.whitbread.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.domain.model.hotel.out.HotelPreferencesResponse;
import uk.co.whitbread.domain.model.hotel.out.RoomTypesInfo;

public interface HotelInfoOutPort {

  HotelInfo getHotelInfo(final String hotelId);

  Map<String, RoomTypesInfo> getRoomTypesInfoByHotelIds(final List<String> hotelIds);

  HotelPreferencesResponse getHotelPreferences(String hotelId, String groupCode, String language);

}
