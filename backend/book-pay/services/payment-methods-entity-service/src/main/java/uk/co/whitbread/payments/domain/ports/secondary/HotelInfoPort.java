package uk.co.whitbread.payments.domain.ports.secondary;

import uk.co.whitbread.payments.domain.model.out.HotelInfo;

public interface HotelInfoPort {
  HotelInfo findHotelPaymentDetails(String hotelCode, String country, String language);
}
