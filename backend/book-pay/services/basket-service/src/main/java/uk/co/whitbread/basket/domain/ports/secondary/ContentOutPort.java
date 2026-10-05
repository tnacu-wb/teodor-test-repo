package uk.co.whitbread.basket.domain.ports.secondary;

import uk.co.whitbread.basket.domain.model.content.out.BusinessNotesResponse;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;

public interface ContentOutPort {
  HotelPaymentInformation getHotelPaymentDetails(String hotelCode, String country, String language);

  BusinessNotesResponse getBusinessNotes(String language);
}
