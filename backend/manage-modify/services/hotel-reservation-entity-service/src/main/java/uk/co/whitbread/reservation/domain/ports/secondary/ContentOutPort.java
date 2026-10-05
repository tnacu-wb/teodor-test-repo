package uk.co.whitbread.reservation.domain.ports.secondary;

import java.util.Optional;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.HotelPaymentInformation;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.IndexHeaderData;
import uk.co.whitbread.reservation.domain.model.out.BusinessNotesResponse;
import uk.co.whitbread.reservation.domain.model.out.HotelInfoResponse;
import uk.co.whitbread.reservation.domain.model.searchrules.out.SearchRules;

public interface ContentOutPort {

  IndexHeaderData getIndexHeaderData(String country, String language);

  BusinessNotesResponse getBusinessNotes(String language);

  HotelPaymentInformation getHotelPaymentInformation(String hotelId,
      String language, String country);

  HotelInfoResponse getHotelInformation(String hotelId, String country, String language);

  SearchRules getSearchRules(String channel, Optional<String> brand);
}
