package uk.co.whitbread.dashboard.infrastructure.rest.client.hotelaccount;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.dashboard.domain.ports.secondary.HotelAccountOutPort;
import uk.co.whitbread.dashboard.infrastructure.rest.client.hotelaccount.service.HotelAccountClient;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.BBStaysRequestV2;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.StaysResponse;


@Component
@RequiredArgsConstructor
@Slf4j
public class HotelAccountOutPortImpl implements HotelAccountOutPort {

  private final HotelAccountClient hotelAccountClient;

  @Override
  public StaysResponse getAccountStays(
      final BBStaysRequestV2 staysRequest,
      final String customerId,
      final String authorization,
      final String bookingChannel,
      final String origin) {
    return hotelAccountClient.getAccountStays(staysRequest, customerId, authorization, bookingChannel, origin);
  }
}
