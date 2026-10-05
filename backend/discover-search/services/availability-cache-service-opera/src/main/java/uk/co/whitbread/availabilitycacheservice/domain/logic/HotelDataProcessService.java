package uk.co.whitbread.availabilitycacheservice.domain.logic;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.BartBrandHotelCode;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.HotelDataProcessPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.RateClassificationLookupPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassification;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

@Slf4j
@AllArgsConstructor
public class HotelDataProcessService implements HotelDataProcessPort {

  private RateClassificationLookupPort rateClassificationLookupPort;

  public List<Hotel> processHotelDtoData(final SearchCriteria searchCriteria, List<Hotel> hotelList) {

    Map<String, RateClassification> defaultRateClassifications = rateClassificationLookupPort
        .getRateClassifications(BartBrandHotelCode.PI, searchCriteria.getLanguage());

    if (defaultRateClassifications != null && !defaultRateClassifications.isEmpty() && !hotelList.isEmpty()) {
      hotelList.forEach(hotel -> processRateClassifications(hotel, defaultRateClassifications));
    }
    return hotelList;
  }

  public void processRateClassifications(Hotel hotel,
      final Map<String, RateClassification> rateClassification) {

    log.debug("hotel Data for rate Classification Processing:: {} {}", hotel.getHotelCode(), hotel.getHotelName());

    if (rateClassification != null) {
      final List<RatePlan> ratePlans =
          rateClassificationLookupPort.processRatePlansWithRateClassification(hotel.getRates(), rateClassification);
      hotel.setRates(ratePlans);
    }
  }

}
