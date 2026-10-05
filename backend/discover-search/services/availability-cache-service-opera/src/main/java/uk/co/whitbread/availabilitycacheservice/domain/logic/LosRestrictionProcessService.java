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
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.LosRestrictionName;
import uk.co.whitbread.availabilitycacheservice.domain.model.los.rules.LosRestriction;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionRulesEvaluator;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;


@Service
@RequiredArgsConstructor
@Slf4j
public class LosRestrictionProcessService implements LosRestrictionPort<Hotel> {

  private final LosRestrictionRulesEvaluator losRestrictionRulesEvaluator;

  public List<Hotel> applyLosRestrictions(final SearchCriteria criteria, final List<Hotel> hotels) {

    if (hotels.isEmpty()) {
      return Collections.emptyList();
    }

    log.debug("applyLosRestrictions processing started for hotels - {} ",
        sanitize(criteria.getHotelCodes()));
    return hotels.stream().map(hotel -> {
      if (hotel.getRates().isEmpty()) {
        log.debug("ratePlans empty from DB, So no LOSRestrictions applied on hotel - {}",
            sanitize(hotel.getHotelCode()));
        return hotel;
      }

      AtomicBoolean hasMlosRestriction = new AtomicBoolean(false);
      final List<RatePlan> ratePlanWithNoRestrictions =
          hotel.getRates().stream().map(ratePlan -> {
            log.debug("RatePlan details - {}", ratePlan);
            final LosRestriction losRestriction = losRestrictionRulesEvaluator.evaluateRestrictionRules(ratePlan,
                criteria);
            log.debug(new ObjectAppendingMarker("LosRestriction", losRestriction.getLosRule()),
                "LosRestriction - {} applied on RatePlan-{} for the Hotel - {}",
                losRestriction.getLosRule(), ratePlan.getClassification(), hotel.getHotelCode());

            if (losRestriction.getLosRule().equals(LosRestrictionName.NO_RESTRICTIONS)) {
              log.debug(
                  "No Restrictions returned from LosRestriction - {} applied on RatePlan-{} for the Hotel - {}. "
                      + "So add the RatePlan to the RatePlan list",
                  losRestriction.getLosRule(), ratePlan.getClassification(), hotel.getHotelCode());
              return ratePlan;
            } else if (losRestriction.getLosRule().equals(LosRestrictionName.MIN_NIGHT_RESTRICTIONS)) {
              log.debug(
                  "MinLOS Restrictions returned from LosRestriction - {} applied on RatePlan-{} for the Hotel - {}. ",
                  losRestriction.getLosRule(), ratePlan.getClassification(), hotel.getHotelCode());
              hasMlosRestriction.set(true);
            }
            return null;
          }).filter(Objects::nonNull).collect(Collectors.toList());

      hotel.setRates(ratePlanWithNoRestrictions);
      if (ratePlanWithNoRestrictions.isEmpty()) {
        log.debug(
            "As ratePlans is empty after LOS Restrictions applied, set available and limitedAvailability to False "
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

  public boolean isLosApplicable(final RatePlan ratePlan, final SearchCriteria criteria) {
    boolean isLosApplicable = false;
    final String hotelCode = criteria.getHotelCodes().get(0);
    final LosRestriction losRestriction = losRestrictionRulesEvaluator.evaluateRestrictionRules(ratePlan, criteria);

    if (!losRestriction.getLosRule().equals(LosRestrictionName.NO_RESTRICTIONS)) {
      log.debug("Restrictions returned from LosRestriction - {} applied on RatePlan-{} for the Hotel - {}. ",
          losRestriction.getLosRule(), ratePlan.getClassification(), hotelCode);
      isLosApplicable = true;
    }

    return isLosApplicable;
  }
}


