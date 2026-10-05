package uk.co.whitbread.ohip.domain.ports.primary;

import uk.co.whitbread.ohip.domain.model.preferences.out.HotelPreferencesResponse;

public interface PreferenceInPort {

  HotelPreferencesResponse getPreferencesForGroup(String hotelId, String groupCode);
}
