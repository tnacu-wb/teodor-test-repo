package uk.co.whitbread.availabilitycacheservice.domain.ports.primary;

import java.util.List;
import java.util.Map;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassification;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

public interface HotelDataProcessPort {

  List<Hotel> processHotelDtoData(final SearchCriteria searchCriteria, List<Hotel> hotelDtoList);

  void processRateClassifications(Hotel hotelDto,
      final Map<String, RateClassification> rateClassification);
}
