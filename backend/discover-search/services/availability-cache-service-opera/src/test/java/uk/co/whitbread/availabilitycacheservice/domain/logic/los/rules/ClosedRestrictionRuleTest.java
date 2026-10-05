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
class ClosedRestrictionRuleTest {

  private ClosedRestrictionRule closedRestrictionRule = new ClosedRestrictionRule();

  private RatePlan hotelRate;

  private SearchCriteria searchCriteria;

  private boolean isApplicable = false;

  @BeforeEach
  void setUp() {
    hotelRate = new RatePlan();
    hotelRate.setCode("Test");
    closedRestrictionRule.registerRule();
    log.info("Rule Name: {}", closedRestrictionRule.getRuleName());
    searchCriteria = buildSearchCriteria();
  }

  @Test
  void testClosedRestrictionIsApplicable() {
    hotelRate.setMinNights(666);
    isApplicable = closedRestrictionRule.isApplicable(hotelRate, searchCriteria);
    assertThat(isApplicable).isTrue();
  }

  @Test
  void testClosedRestrictionIsNotApplicable() {
    hotelRate.setMinNights(0);
    isApplicable = closedRestrictionRule.isApplicable(hotelRate, searchCriteria);
    assertThat(isApplicable).isFalse();
  }

  @Test
  void testCtaRestrictionIsNotApplicableWithMinNightAsAnyOtherNum() {
    hotelRate.setMinNights(9);
    isApplicable = closedRestrictionRule.isApplicable(hotelRate, searchCriteria);
    assertThat(isApplicable).isFalse();
  }

  private SearchCriteria buildSearchCriteria() {
    String arrival = LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
    String departure =
        LocalDate.parse(LocalDate.now().plusDays(2).toString(), DateTimeFormatter.ISO_LOCAL_DATE)
            .toString();
    log.info("Arrival:{}, Departure:{}", arrival, departure);
    return SearchCriteria.builder().arrival(arrival).departure(departure).build();
  }
}