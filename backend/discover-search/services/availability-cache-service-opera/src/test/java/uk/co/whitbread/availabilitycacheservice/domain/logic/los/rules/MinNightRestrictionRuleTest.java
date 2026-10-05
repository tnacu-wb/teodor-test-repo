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

public class MinNightRestrictionRuleTest {

  private final MinNightRestrictionRule minNightRestrictionRule = new MinNightRestrictionRule();

  private RatePlan hoteRate;

  private SearchCriteria searchCriteria;

  @BeforeEach
  public void setUp() throws Exception {
    hoteRate = new RatePlan();
    hoteRate.setCode("Test");
    hoteRate.setClassification("A");
    log.info("Rule Name:" + minNightRestrictionRule.getRuleName());
  }

  @Test
  public void testMinNightRestrictionIsApplicableWithMinNightAs2() {
    hoteRate.setMinNights(2);
    hoteRate.setMaxNights(0);
    SearchCriteria searchCriteria = buildSearchCriteria(1);
    boolean isApplicable = minNightRestrictionRule.isApplicable(hoteRate, searchCriteria);
    assertThat(isApplicable).isTrue();

    hoteRate.setMinNights(3);
    hoteRate.setMaxNights(0);
    searchCriteria = buildSearchCriteria(2);
    isApplicable = minNightRestrictionRule.isApplicable(hoteRate, searchCriteria);
    assertThat(isApplicable).isTrue();

    hoteRate.setMinNights(4);
    hoteRate.setMaxNights(0);
    searchCriteria = buildSearchCriteria(3);
    isApplicable = minNightRestrictionRule.isApplicable(hoteRate, searchCriteria);
    assertThat(isApplicable).isTrue();
  }

  @Test
  public void testMinNightRestrictionIsApplicableWithMinNightAs3() {
    hoteRate.setMinNights(3);
    hoteRate.setMaxNights(0);
    final SearchCriteria searchCriteria = buildSearchCriteria(2);
    final boolean isApplicable = minNightRestrictionRule.isApplicable(hoteRate, searchCriteria);
    assertThat(isApplicable).isTrue();
  }

  @Test
  public void testMinNightRestrictionIsApplicableWithMinNightAs4() {
    hoteRate.setMinNights(4);
    hoteRate.setMaxNights(0);
    final SearchCriteria searchCriteria = buildSearchCriteria(3);
    final boolean isApplicable = minNightRestrictionRule.isApplicable(hoteRate, searchCriteria);
    assertThat(isApplicable).isTrue();
  }

  @Test
  public void testMinNightRestrictionIsNotApplicableWithNoMaxNight() {
    hoteRate.setMinNights(0);
    final SearchCriteria searchCriteria = buildSearchCriteria(3);
    final boolean isApplicable = minNightRestrictionRule.isApplicable(hoteRate, searchCriteria);
    assertThat(isApplicable).isFalse();
  }

  @Test
  public void testMinNightRestrictionIsNotApplicableWithCustomerNightEqualToMinNightsRestriction() {
    hoteRate.setMinNights(2);
    hoteRate.setMaxNights(0);
    final SearchCriteria searchCriteria = buildSearchCriteria(2);
    final boolean isApplicable = minNightRestrictionRule.isApplicable(hoteRate, searchCriteria);
    assertThat(isApplicable).isFalse();
  }

  @Test
  public void testMinNightRestrictionIsNotApplicableWithMinNightAs5() {
    hoteRate.setMinNights(5);
    hoteRate.setMaxNights(0);
    final SearchCriteria searchCriteria = buildSearchCriteria(3);
    final boolean isApplicable = minNightRestrictionRule.isApplicable(hoteRate, searchCriteria);
    assertThat(isApplicable).isTrue();
  }

  @Test
  public void testMinNightRestrictionIsNotApplicableWithMinNightAs1() {
    hoteRate.setMinNights(1);
    hoteRate.setMaxNights(0);
    final boolean isApplicable = minNightRestrictionRule.isApplicable(hoteRate, searchCriteria);
    assertThat(isApplicable).isFalse();
  }

  @Test
  public void testMinNightRestrictionIsNotApplicableWithMinAndMaxNight() {
    hoteRate.setMinNights(1);
    hoteRate.setMaxNights(1);
    final boolean isApplicable = minNightRestrictionRule.isApplicable(hoteRate, searchCriteria);
    assertThat(isApplicable).isFalse();
  }

  private SearchCriteria buildSearchCriteria(final int numberOfNightStays) {
    final String arrival = LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
    final String departure = LocalDate.parse(LocalDate.now().plusDays(numberOfNightStays).toString(),
        DateTimeFormatter.ISO_LOCAL_DATE).toString();
    log.info("Arrival:{}, Departure:{}", arrival, departure);
    return SearchCriteria.builder().arrival(arrival).departure(departure).build();
  }
}