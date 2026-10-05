package uk.co.whitbread.availabilitycacheservice.domain.logic.los.rules;

import static uk.co.whitbread.availabilitycacheservice.domain.model.enums.LosRestrictionName.CLOSED_RESTRICTIONS;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.enums.LosRestrictionName;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionRule;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.LosRestrictionRulesEvaluator;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;


@Slf4j
@Service
public class ClosedRestrictionRule implements LosRestrictionRule {

  private static final int CLOSED = 666;
  private LosRestrictionName ruleName;

  public ClosedRestrictionRule() {
    this.ruleName = CLOSED_RESTRICTIONS;
  }

  /**
   * Checks if the Closed Restriction Rule is applicable for the given rate plan and search criteria.
   *
   * <p>The rule is applicable if the {@code hotelRate} is not null and its {@code minNights} value equals the CLOSED
   * constant.
   *
   * @param hotelRate      the {@link RatePlan} to evaluate
   * @param searchCriteria the {@link SearchCriteria} for the evaluation
   * @return {@code true} if the rule is applicable, {@code false} otherwise
   */
  @Override
  public boolean isApplicable(RatePlan hotelRate, SearchCriteria searchCriteria) {
    boolean isApplicable = false;
    log.trace("Closed restrictions rule execution....");
    if (null != hotelRate && hotelRate.getMinNights() == CLOSED) {
      isApplicable = true;
    }
    return isApplicable;
  }

  @Override
  public LosRestrictionName getRuleName() {
    return ruleName;
  }

  @PostConstruct
  @Override
  public void registerRule() {
    LosRestrictionRulesEvaluator.registerRule(this);
    log.debug("Rule:{} registered", CLOSED_RESTRICTIONS.name());
  }
}