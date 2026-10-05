package uk.co.whitbread.dashboard.domain.ports.secondary;

import uk.co.whitbread.hotel.account.generated.hotelaccount.model.BBStaysRequestV2;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.StaysResponse;

public interface HotelAccountOutPort {

  StaysResponse getAccountStays(BBStaysRequestV2 staysRequest, String customerId,
      String authorization, String bookingChannel, String origin);
  
}
