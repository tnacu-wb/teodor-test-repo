package uk.co.whitbread.availabilitycacheservice.domain.logic.los.rules;

import static java.time.temporal.ChronoUnit.DAYS;
import static uk.co.whitbread.availabilitycacheservice.domain.model.enums.LosRestrictionName.MAX_NIGHT_RESTRICTIONS;

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

@Service
@Slf4j
@DependsOn("minNightRestrictionRule")
public class MaxNightRestrictionRule implements LosRestrictionRule {

  private static final int DEFAULT_MIN_NIGHT = 0;
  private LosRestrictionName ruleName;

  public MaxNightRestrictionRule() {
    this.ruleName = MAX_NIGHT_RESTRICTIONS;
  }

  /**
   * Max Night Restriction Rule.
   *
   * <p><b>Rule:</b><br>
   * A {@code RatePlan} will be excluded if:
   * <ul>
   *   <li>No {@code minNightRestriction} is defined (equals 0),</li>
   *   <li>and a {@code maxNightsRestriction} is defined (greater than 0),</li>
   *   <li>and The {@code stayNights} is greater than the {@code maxNightsRestriction}.</li>
   * </ul>
   * <b>Examples:</b>
   * <pre>
   * minNightRestriction = 0, maxNightsRestriction = 2, stayNights = 3
   *   → Excluded (not available)
   * minNightRestriction = 0, maxNightsRestriction = 2, stayNights = 2
   *   → Included (available)
   * minNightRestriction = 0, maxNightsRestriction = 4, stayNights = 5
   *   → Excluded (not available)
   * minNightRestriction = 0, maxNightsRestriction = 4, stayNights = 4
   *   → Included (available)
   * </pre>
   */
  @Override
  public boolean isApplicable(final RatePlan hotelRate, final SearchCriteria searchCriteria) {
    log.trace("Executing MaxNightRestrictionRule...");
    boolean isMaxNightRestrictionApplicable = false;
    if (hotelRate != null && hotelRate.getMinNights() == DEFAULT_MIN_NIGHT) {
      final int maxNightsRestriction = hotelRate.getMaxNights();
      final long stayNights = DAYS.between(LocalDate.parse(searchCriteria.getArrival()),
          LocalDate.parse(searchCriteria.getDeparture()));
      log.trace("minNights - {} , maxNightsRestriction - {} and stayNights - {} ", hotelRate.getMinNights(),
          maxNightsRestriction,
          stayNights);
      if (maxNightsRestriction > 0 && stayNights > maxNightsRestriction) {
        isMaxNightRestrictionApplicable = true;
      }
    }
    return isMaxNightRestrictionApplicable;
  }

  @Override
  public LosRestrictionName getRuleName() {
    return ruleName;
  }

  @PostConstruct
  @Override
  public void registerRule() {
    LosRestrictionRulesEvaluator.registerRule(this);
    log.debug("Rule:{} registered", MAX_NIGHT_RESTRICTIONS.name());
  }
}
