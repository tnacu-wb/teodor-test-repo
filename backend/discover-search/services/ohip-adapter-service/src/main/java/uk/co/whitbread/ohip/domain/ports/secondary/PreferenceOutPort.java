package uk.co.whitbread.ohip.domain.ports.secondary;

import uk.co.whitbread.ohip.domain.model.preferences.out.HotelPreferencesResponse;

public interface PreferenceOutPort {

  HotelPreferencesResponse getPreferenceForGroup(String hotelId, String groupCode);
}
