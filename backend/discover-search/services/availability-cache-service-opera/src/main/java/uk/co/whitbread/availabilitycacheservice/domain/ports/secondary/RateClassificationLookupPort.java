package uk.co.whitbread.availabilitycacheservice.domain.ports.secondary;


import java.util.List;
import java.util.Map;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.BartBrandHotelCode;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassification;

public interface RateClassificationLookupPort {

  Map<String, RateClassification> getRateClassifications(final BartBrandHotelCode hotelBrand,
      final String language, final String hotelCode);

  Map<String, RateClassification> getRateClassifications(final BartBrandHotelCode hotelBrand,
      final String language);

  Map<String, RateClassification> getRateClassificationsOperaBartHotels(
      final BartBrandHotelCode hotelBrand, final String language, final String country);

  List<RatePlan> processRatePlansWithRateClassification(List<RatePlan> ratePlans,
      final Map<String, RateClassification> rateClassifications);

  void populateRatePlanDtoWithRateClassification(RatePlan ratePlan,
      final RateClassification rateClassification);
}
