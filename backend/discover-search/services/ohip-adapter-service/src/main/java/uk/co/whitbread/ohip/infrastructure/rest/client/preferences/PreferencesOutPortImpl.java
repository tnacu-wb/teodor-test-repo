package uk.co.whitbread.ohip.infrastructure.rest.client.preferences;

import lombok.RequiredArgsConstructor;
import uk.co.whitbread.ohip.domain.model.preferences.out.HotelPreferencesResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.PreferenceOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.preferences.mapper.HotelPreferencesMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.preferences.ohip.OhipPreferencesClient;

@RequiredArgsConstructor
public class PreferencesOutPortImpl implements PreferenceOutPort {

  private final OhipPreferencesClient ohipPreferencesClient;
  private final HotelPreferencesMapper hotelPreferencesMapper;

  @Override
  public HotelPreferencesResponse getPreferenceForGroup(String hotelId, String groupCode) {
    var ohipResponse = ohipPreferencesClient.getPreferencesForGroup(hotelId, groupCode);
    return hotelPreferencesMapper.toDomainModel(ohipResponse);
  }
}
