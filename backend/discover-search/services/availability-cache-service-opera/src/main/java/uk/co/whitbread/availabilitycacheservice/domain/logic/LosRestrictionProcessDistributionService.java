package uk.co.whitbread.availabilitycacheservice.domain.logic;

import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.springframework.stereotype.Service;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionHotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionRatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.LosRestrictionName;
import uk.co.whitbread.availabilitycacheservice.domain.model.los.rules.LosRestriction;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionRulesEvaluator;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

@RequiredArgsConstructor
@Slf4j
@Service("losRestrictionProcessDistributionService")
public class LosRestrictionProcessDistributionService implements LosRestrictionPort<DistributionHotel> {

  private final LosRestrictionRulesEvaluator losRestrictionRulesEvaluator;

  public List<DistributionHotel> applyLosRestrictions(final SearchCriteria criteria,
      final List<DistributionHotel> hotels) {

    if (hotels.isEmpty()) {
      return Collections.emptyList();
    }

    log.info("Distribution applyLosRestrictions processing started for hotels - {} ",
        sanitize(criteria.getHotelCodes()));
    return hotels.stream().map(hotel -> {
      if (hotel.getRates().isEmpty()) {
        log.info("Distribution ratePlans empty from DB, So no LOSRestrictions applied on hotel - {}",
            hotel.getHotelCode());
        return hotel;
      }

      AtomicBoolean hasMlosRestriction = new AtomicBoolean(false);
      final List<DistributionRatePlan> ratePlanWithNoRestrictions =
          hotel.getRates().stream().map(ratePlan -> {
            log.debug("Distribution RatePlan details - {}", ratePlan);

            RatePlan distrLosRatePlan = RatePlan.builder().minNights(ratePlan.getMinNights())
                .maxNights(ratePlan.getMaxNights()).build();
            final LosRestriction losRestriction = losRestrictionRulesEvaluator.evaluateRestrictionRules(
                distrLosRatePlan, criteria);

            log.debug(new ObjectAppendingMarker("Distribution LosRestriction", losRestriction.getLosRule()),
                "LosRestriction - {} applied on RatePlan-{} for the Hotel - {}",
                losRestriction.getLosRule(), ratePlan.getClassification(), hotel.getHotelCode());

            if (losRestriction.getLosRule().equals(LosRestrictionName.NO_RESTRICTIONS)) {
              log.debug(
                  "Distribution No Restrictions returned from LosRestriction - {} applied on RatePlan-{} "
                      + "for the Hotel - {}. So add the RatePlan to the RatePlan list",
                  losRestriction.getLosRule(), ratePlan.getClassification(), hotel.getHotelCode());
              return ratePlan;
            } else if (losRestriction.getLosRule().equals(LosRestrictionName.MIN_NIGHT_RESTRICTIONS)) {
              log.info(
                  "Distribution MinLOS Restrictions returned from LosRestriction - {} applied on RatePlan-{} for the "
                      + "Hotel - {}. ",
                  losRestriction.getLosRule(), ratePlan.getClassification(), hotel.getHotelCode());
              hasMlosRestriction.set(true);
            }
            return null;
          }).filter(Objects::nonNull).collect(Collectors.toList());

      hotel.setRates(ratePlanWithNoRestrictions);
      if (ratePlanWithNoRestrictions.isEmpty()) {
        log.info(
            "Distribution, As ratePlans is empty after LOS Restrictions applied, set available and "
                + "limitedAvailability to False "
                + "for hotel - {}",
            hotel.getHotelCode());
        hotel.setAvailable(false);
        hotel.setLimitedAvailability(false);
      }
      if (criteria.isFlagMlos()) {
        hotel.setHasMlosRestriction(hasMlosRestriction.get());
      }
      return hotel;
    }).collect(Collectors.toList());
  }

  @Override
  public boolean isLosApplicable(RatePlan ratePlan, SearchCriteria criteria) {
    throw new UnsupportedOperationException(
        "Distribution LosRestrictionProcessDistributionService is not applicable for this method");
  }

}


