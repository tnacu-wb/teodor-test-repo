package uk.co.whitbread.ohip.domain.logic;

import static uk.co.whitbread.ohip.domain.utils.SanitizingUtils.sanitize;

import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.ohip.domain.model.hotel.out.RoomTypesInfo;
import uk.co.whitbread.ohip.domain.ports.primary.HotelInfoInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelInfoOutPort;

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
  public RoomTypesInfo getRoomTypesInfo(String hotelId) {
    log.debug("Entered getRoomTypesInfo for hotelId={}", sanitize(hotelId));
    return this.hotelInfoOutPort.getRoomTypesInfo(hotelId);
  }

}
