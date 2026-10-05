package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;

import java.util.List;
import java.util.Map;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.BartBrandHotelCode;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassification;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;

public interface ContentClientLookupPort {

  Map<String, RateClassification> getRateClassifications(
      final BartBrandHotelCode hotelBrand, final String language, final String hotelCode,
      final String country);

  HotelsCityTaxInfo getHotelsCityTaxInfo(String country, String language, List<String> hotelCodes);
}
