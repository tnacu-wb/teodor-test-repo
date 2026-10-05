package uk.co.whitbread.availabilitycacheservice.domain.logic.los.rules;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

@Slf4j
public class CtaRestrictionRuleTest {

  private CtaRestrictionRule ctaRestrictionRule = new CtaRestrictionRule();

  private RatePlan hoteRate;

  private SearchCriteria searchCriteria;

  private boolean isApplicable = false;

  @BeforeEach
  public void setUp() throws Exception {
    hoteRate = new RatePlan();
    hoteRate.setCode("Test");
    hoteRate.setClassification("A");
    log.info("Rule Name:" + ctaRestrictionRule.getRuleName());
    searchCriteria = buildSearchCriteria(2);
  }

  @Test
  public void testCtaRestrictionIsApplicable() {
    hoteRate.setMinNights(999);
    isApplicable = ctaRestrictionRule.isApplicable(hoteRate, searchCriteria);
    assertThat(isApplicable).isTrue();
  }

  @Test
  public void testCtaRestrictionIsNotApplicable() {
    hoteRate.setMinNights(0);
    isApplicable = ctaRestrictionRule.isApplicable(hoteRate, searchCriteria);
    assertThat(isApplicable).isFalse();
  }

  @Test
  public void testCtaRestrictionIsNotApplicableWithMinNightAsAnyOtherNum() {
    hoteRate.setMinNights(9);
    isApplicable = ctaRestrictionRule.isApplicable(hoteRate, searchCriteria);
    assertThat(isApplicable).isFalse();
  }

  private SearchCriteria buildSearchCriteria(final int numberOfNightStays) {
    String arrival = LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
    String departure = LocalDate.parse(LocalDate.now().plusDays(numberOfNightStays).toString(),
        DateTimeFormatter.ISO_LOCAL_DATE).toString();
    log.info("Arrival:{}, Departure:{}", arrival, departure);
    return SearchCriteria.builder().arrival(arrival).departure(departure).build();
  }
}