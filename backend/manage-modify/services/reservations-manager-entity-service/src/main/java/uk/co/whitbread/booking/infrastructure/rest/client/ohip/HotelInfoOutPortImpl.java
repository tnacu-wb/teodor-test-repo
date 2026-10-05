package uk.co.whitbread.booking.infrastructure.rest.client.ohip;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import uk.co.whitbread.booking.domain.model.history.out.Booking;
import uk.co.whitbread.booking.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.HotelInfoDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.service.OhipAdapterClient;

@Slf4j
@RequiredArgsConstructor
public class HotelInfoOutPortImpl implements HotelInfoOutPort {

  private final OhipAdapterClient ohipAdapterClient;

  @Override
  public Map<String, String> getHotelInfo(List<Booking> bookings) {
    Set<String> hotelIds = bookings.stream()
        .map(Booking::getHotelCode)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    var response = ohipAdapterClient.fetchMultipleHotelsInfo(hotelIds);

    return response.stream()
        .filter(hotelInfoDto -> ObjectUtils.allNotNull(hotelInfoDto.getHotelId(),
            hotelInfoDto.getHotelCountryCode(), hotelInfoDto.getHotelTimeZone()))
        .collect(Collectors.toMap(
            HotelInfoDto::getHotelId,
            HotelInfoDto::getHotelCountryCode
        ));
  }
}
