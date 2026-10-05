package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Value;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;

@Value
@Builder
public class HotelsCityTaxInfo {
  List<String> hotelsWithCityTax;
  Map<String, HotelCityTax> hotelsCityTaxes;
}
