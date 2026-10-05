package uk.co.whitbread.ohip.domain.logic;

import lombok.RequiredArgsConstructor;
import uk.co.whitbread.ohip.domain.model.preferences.out.HotelPreferencesResponse;
import uk.co.whitbread.ohip.domain.ports.primary.PreferenceInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.PreferenceOutPort;

@RequiredArgsConstructor
public class PreferenceInPortImpl implements PreferenceInPort {

  private final PreferenceOutPort preferenceOutPort;

  @Override
  public HotelPreferencesResponse getPreferencesForGroup(String hotelId, String groupCode) {
    return preferenceOutPort.getPreferenceForGroup(hotelId, groupCode);
  }
}
