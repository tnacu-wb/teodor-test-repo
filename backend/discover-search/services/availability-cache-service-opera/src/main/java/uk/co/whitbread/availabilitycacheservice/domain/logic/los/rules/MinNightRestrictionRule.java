package uk.co.whitbread.availabilitycacheservice.domain.logic.los.rules;


import static java.time.temporal.ChronoUnit.DAYS;
import static uk.co.whitbread.availabilitycacheservice.domain.model.enums.LosRestrictionName.MIN_NIGHT_RESTRICTIONS;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.LosRestrictionName;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionRule;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionRulesEvaluator;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

@Slf4j
@Service
@DependsOn("minMaxNightRestrictionRule")
public class MinNightRestrictionRule implements LosRestrictionRule {

  private static final int DEFAULT_MAX_NIGHT = 0;
  private LosRestrictionName ruleName;

  public MinNightRestrictionRule() {
    this.ruleName = MIN_NIGHT_RESTRICTIONS;
  }

  /**
   * Min Night Restriction Rule.
   *
   * <p><b>Rule:</b><br>
   * A {@code RatePlan} will be excluded if:
   * <ul>
   *   <li>A {@code minNightRestriction} is defined (greater than 1),</li>
   *   <li>and no {@code maxNightsRestriction} is defined (equals 0),</li>
   *   <li>and the {@code stayNights} is less than the {@code minNightRestriction}.</li>
   * </ul>
   * <b>Examples:</b>
   * <pre>
   * minNightRestriction = 3, maxNightsRestriction = 0, stayNights = 2
   *   → Excluded (not available)
   * minNightRestriction = 3, maxNightsRestriction = 0, stayNights = 3
   *   → Included (available)
   * minNightRestriction = 2, maxNightsRestriction = 0, stayNights = 1
   *   → Excluded (not available)
   * minNightRestriction = 2, maxNightsRestriction = 0, stayNights = 2
   *   → Included (available)
   * </pre>
   */
  @Override
  public boolean isApplicable(final RatePlan hotelRate, final SearchCriteria searchCriteria) {
    log.trace("MinNightRestrictionRule execution....");
    return (hotelRate != null && hotelRate.getMaxNights() == DEFAULT_MAX_NIGHT
        && isMinNightRestrictionApplicable(hotelRate.getMinNights(), searchCriteria));
  }

  private boolean isMinNightRestrictionApplicable(final int minNightRestriction, final SearchCriteria searchCriteria) {
    boolean isMinNightRestrictionApplicable = false;
    if (minNightRestriction > 1) {
      final long stayNights = DAYS.between(LocalDate.parse(searchCriteria.getArrival()),
          LocalDate.parse(searchCriteria.getDeparture()));
      log.trace("minNightRestriction - {} and stayNights - {} ", minNightRestriction, stayNights);
      if ((stayNights < minNightRestriction)) {
        isMinNightRestrictionApplicable = true;
      }
    }
    return isMinNightRestrictionApplicable;
  }

  @Override
  public LosRestrictionName getRuleName() {
    return ruleName;
  }

  @PostConstruct
  public void registerRule() {
    LosRestrictionRulesEvaluator.registerRule(this);
    log.debug("Rule:{} registered", MIN_NIGHT_RESTRICTIONS.name());
  }
}
