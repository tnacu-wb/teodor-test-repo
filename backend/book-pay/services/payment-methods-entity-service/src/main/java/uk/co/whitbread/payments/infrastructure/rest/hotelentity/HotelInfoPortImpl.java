package uk.co.whitbread.payments.infrastructure.rest.hotelentity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.payments.domain.ports.secondary.HotelEntityPort;
import uk.co.whitbread.payments.infrastructure.rest.hotelentity.service.HotelEntityClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelInfoPortImpl implements HotelEntityPort {

  private final HotelEntityClient hotelEntityClient;

  @Override
  public String findHotelCountry(String hotelCode) {
    var hotelInfo = hotelEntityClient.getHotelInfo(hotelCode);
    return hotelInfo != null ? hotelInfo.getHotelCountryCode() : null;
  }
}
