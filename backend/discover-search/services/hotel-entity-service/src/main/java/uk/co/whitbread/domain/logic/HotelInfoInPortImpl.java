package uk.co.whitbread.domain.logic;

import static uk.co.whitbread.infrastructure.rest.client.utils.SanitizingUtils.sanitize;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.domain.model.hotel.out.HotelPreferencesResponse;
import uk.co.whitbread.domain.model.hotel.out.RoomTypesInfo;
import uk.co.whitbread.domain.ports.primary.HotelInfoInPort;
import uk.co.whitbread.domain.ports.secondary.HotelInfoOutPort;

@Slf4j
public class HotelInfoInPortImpl implements HotelInfoInPort {

  private final HotelInfoOutPort hotelInfoOutPort;

  public HotelInfoInPortImpl(final HotelInfoOutPort hotelInfoOutPort) {
    this.hotelInfoOutPort = hotelInfoOutPort;
  }

  @Override
  public HotelInfo getHotelInfo(String hotelId) {
    log.debug("Entered getHotelInfo for hotelId={}", sanitize(hotelId));
    return this.hotelInfoOutPort.getHotelInfo(hotelId);
  }

  @Override
  public Map<String, RoomTypesInfo> getRoomTypesInfo(List<String> hotelIds) {
    log.debug("Entered getRoomTypesInfo for hotelIds={}", hotelIds);
    return this.hotelInfoOutPort.getRoomTypesInfoByHotelIds(hotelIds);
  }

  @Override
  public HotelPreferencesResponse getHotelPreferences(String hotelId, String groupCode, String language) {
    return hotelInfoOutPort.getHotelPreferences(hotelId, groupCode, language);
  }
}
