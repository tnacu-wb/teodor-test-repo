package uk.co.whitbread.availabilitycacheservice.domain.logic.los.rules;

import static java.time.temporal.ChronoUnit.DAYS;
import static uk.co.whitbread.availabilitycacheservice.domain.model.enums.LosRestrictionName.MIN_MAX_NIGHT_RESTRICTIONS;

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
@DependsOn("ctaRestrictionRule")
public class MinMaxNightRestrictionRule implements LosRestrictionRule {

  private static final int DEFAULT_MIN_NIGHT = 0;
  private static final int DEFAULT_MAX_NIGHT = 0;
  private LosRestrictionName ruleName;

  public MinMaxNightRestrictionRule() {
    this.ruleName = MIN_MAX_NIGHT_RESTRICTIONS;
  }

  /**
   * Min-Max Night Restriction Rule.
   *
   * <p><b>Rule:</b><br>
   * A {@code RatePlan} will be excluded if:
   * <ul>
   *   <li>Both {@code minNightRestriction} and {@code maxNightsRestriction} are defined (not equal to 0),</li>
   *   <li>And the {@code stayNights} is shorter than {@code minNightRestriction}, or greater than {@code
   *   maxNightsRestriction}.</li>
   * </ul>
   * <b>Examples:</b>
   * <pre>
   * minNightRestriction = 2, maxNightsRestriction = 4, stayNights = 1
   *   → Excluded (not available)
   * minNightRestriction = 2, maxNightsRestriction = 4, stayNights = 5
   *   → Excluded (not available)
   * minNightRestriction = 2, maxNightsRestriction = 4, stayNights = 2
   *   → Included (available)
   * minNightRestriction = 2, maxNightsRestriction = 4, stayNights = 3
   *   → Included (available)
   * minNightRestriction = 2, maxNightsRestriction = 4, stayNights = 4
   *   → Included (available)
   * </pre>
   */
  @Override
  public boolean isApplicable(final RatePlan hotelRate, final SearchCriteria searchCriteria) {
    log.trace("Executing MinMaxNightRestrictionRule...");

    if (hotelRate != null && hotelRate.getMinNights() != DEFAULT_MIN_NIGHT
        && hotelRate.getMaxNights() != DEFAULT_MAX_NIGHT) {
      return isMinMaxNightRestrictionApplicable(hotelRate.getMinNights(), hotelRate.getMaxNights(), searchCriteria);
    }
    return false;
  }

  private boolean isMinMaxNightRestrictionApplicable(final int minNightsRestriction, final int maxNightsRestriction,
      final SearchCriteria searchCriteria) {
    final long stayNights = DAYS.between(LocalDate.parse(searchCriteria.getArrival()),
        LocalDate.parse(searchCriteria.getDeparture()));
    log.trace("minNightsRestriction - {} , maxNightsRestriction - {} and stayNights - {} ", minNightsRestriction,
        maxNightsRestriction, stayNights);

    return stayNights < minNightsRestriction || stayNights > maxNightsRestriction;
  }

  @Override
  public LosRestrictionName getRuleName() {
    return ruleName;
  }

  @PostConstruct
  @Override
  public void registerRule() {
    LosRestrictionRulesEvaluator.registerRule(this);
    log.debug("Rule:{} registered", MIN_MAX_NIGHT_RESTRICTIONS.name());
  }

}
