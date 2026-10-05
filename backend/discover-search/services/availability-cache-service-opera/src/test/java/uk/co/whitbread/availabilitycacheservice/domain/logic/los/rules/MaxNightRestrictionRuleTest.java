package uk.co.whitbread.availabilitycacheservice.domain.logic.los.rules;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

@Slf4j

public class MaxNightRestrictionRuleTest {

  private final MaxNightRestrictionRule maxNightRestrictionRule = new MaxNightRestrictionRule();

  @Test
  public void shouldReturnTrueForMinNight5() {
    final SearchCriteria searchCriteria = buildSearchCriteria(4);
    final RatePlan hotelRate = buildRatePlan(5, 3);
    assertThat(maxNightRestrictionRule.isApplicable(hotelRate, searchCriteria)).isFalse();
  }

  @Test
  public void shouldReturnFalseForMinNight0() {
    final SearchCriteria searchCriteria = buildSearchCriteria(2);
    RatePlan hotelRate = buildRatePlan(0, 1);
    assertThat(maxNightRestrictionRule.isApplicable(hotelRate, searchCriteria)).isTrue();

    hotelRate = buildRatePlan(5, 0);
    assertThat(maxNightRestrictionRule.isApplicable(hotelRate, searchCriteria)).isFalse();

    hotelRate = buildRatePlan(5, 4);
    assertThat(maxNightRestrictionRule.isApplicable(hotelRate, searchCriteria)).isFalse();
  }

  @Test
  public void shouldReturnFalseWhenRatePlanIsNull() {
    final SearchCriteria searchCriteria = buildSearchCriteria(2);
    assertThat(maxNightRestrictionRule.isApplicable(null, searchCriteria)).isFalse();
  }

  private RatePlan buildRatePlan(final int minNight, final int maxNight) {
    return RatePlan.builder()
        .code("Test")
        .classification("A")
        .minNights(minNight)
        .maxNights(maxNight)
        .build();
  }

  private SearchCriteria buildSearchCriteria(final int numberOfNightStays) {
    String arrival = LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
    String departure = LocalDate.parse(LocalDate.now().plusDays(numberOfNightStays).toString(),
        DateTimeFormatter.ISO_LOCAL_DATE).toString();
    log.info("Arrival:{}, Departure:{}", arrival, departure);
    return SearchCriteria.builder().arrival(arrival).departure(departure).build();
  }

}
