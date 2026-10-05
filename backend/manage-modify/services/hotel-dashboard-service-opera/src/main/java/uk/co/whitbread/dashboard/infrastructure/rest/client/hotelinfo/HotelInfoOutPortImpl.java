package uk.co.whitbread.dashboard.infrastructure.rest.client.hotelinfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.HotelInfo;
import uk.co.whitbread.dashboard.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.dashboard.infrastructure.rest.client.hotelinfo.service.HotelInfoClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelInfoOutPortImpl implements HotelInfoOutPort {

  private final HotelInfoClient hotelInfoClient;

  @Override
  public HotelInfo getHotelInfo(final String hotelCode) {
    return hotelInfoClient.getHotelInfo(hotelCode);
  }

}
