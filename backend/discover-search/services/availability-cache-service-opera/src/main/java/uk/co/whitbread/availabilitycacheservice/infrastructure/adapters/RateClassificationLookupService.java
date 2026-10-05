package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static java.util.Optional.ofNullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.BartBrandHotelCode;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.ContentClientLookupPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.RateClassificationLookupPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice.RateClassification;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateClassificationLookupService implements RateClassificationLookupPort {

  private final ContentClientLookupPort contentClientLookupPort;

  @Override
  public Map<String, RateClassification> getRateClassifications(
      final BartBrandHotelCode hotelBrand, final String language, final String hotelCode) {
    return contentClientLookupPort
        .getRateClassifications(hotelBrand, language, hotelCode, null);
  }

  @Override
  public Map<String, RateClassification> getRateClassifications(final BartBrandHotelCode hotelBrand,
      final String language) {
    return getRateClassifications(hotelBrand, language, null);
  }

  @Override
  public Map<String, RateClassification> getRateClassificationsOperaBartHotels(
      BartBrandHotelCode hotelBrand, String language, String country) {
    return contentClientLookupPort
        .getRateClassifications(hotelBrand, language, null, country);
  }

  @Override
  public List<RatePlan> processRatePlansWithRateClassification(List<RatePlan> ratePlans,
      final Map<String, RateClassification> rateClassifications) {
    if (ratePlans.isEmpty()) {
      log.debug("ratePlan details are null, and no processing of RateClassifications to RatePlans");
      return Collections.emptyList();
    }

    ratePlans = ratePlans.stream()
        .map(ratePlanDto -> {
          ofNullable(rateClassifications)
              .flatMap(map -> ofNullable(map.get(ratePlanDto.getClassification())))
              .ifPresent(
                  rateClassification -> populateRatePlanDtoWithRateClassification(ratePlanDto, rateClassification));
          return ratePlanDto;
        })
        .collect(Collectors.toList());

    return ratePlans;
  }

  @Override
  public void populateRatePlanDtoWithRateClassification(RatePlan ratePlan,
      final RateClassification rateClassification) {
    ratePlan.setName(rateClassification.getName());
    ratePlan.setDescription(rateClassification.getDescription());
    ratePlan.setOrder(rateClassification.getOrder());
    ratePlan.setNotes(rateClassification.getNotes());
  }

}
