package uk.co.whitbread.availabilitycacheservice.domain.logic.los.rules;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.RatePlan;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

@Slf4j

public class MinMaxNightRestrictionRuleTest {

  private final MinMaxNightRestrictionRule minMaxNightRestrictionRule = new MinMaxNightRestrictionRule();

  @Test
  public void shouldReturnTrueForMinNight4AndMaxNight2() {
    final SearchCriteria searchCriteria = buildSearchCriteria(2);
    RatePlan hotelRate = buildHotelAvailabilitiesResultSet(4, 2);
    assertThat(minMaxNightRestrictionRule.isApplicable(hotelRate, searchCriteria)).isTrue();
  }

  @Test
  public void shouldReturnTrueForMinNight3AndMaxNight1() {
    final SearchCriteria searchCriteria = buildSearchCriteria(3);
    RatePlan hotelRate = buildHotelAvailabilitiesResultSet(3, 1);
    assertThat(minMaxNightRestrictionRule.isApplicable(hotelRate, searchCriteria)).isTrue();
  }

  @Test
  public void shouldReturnTrueForMinNight4AndMaxNight1With2NightStay() {
    final SearchCriteria searchCriteria = buildSearchCriteria(2);
    RatePlan hotelRate = buildHotelAvailabilitiesResultSet(4, 1);
    assertThat(minMaxNightRestrictionRule.isApplicable(hotelRate, searchCriteria)).isTrue();
  }

  @Test
  public void shouldReturnTrueForMinNight4AndMaxNight1With3NightStay() {
    final SearchCriteria searchCriteria = buildSearchCriteria(3);
    RatePlan hotelRate = buildHotelAvailabilitiesResultSet(4, 1);
    assertThat(minMaxNightRestrictionRule.isApplicable(hotelRate, searchCriteria)).isTrue();
  }

  @Test
  public void shouldReturnFalseForMinNight0AndMaxNight1() {
    final SearchCriteria searchCriteria = buildSearchCriteria(4);
    RatePlan hotelRate = buildHotelAvailabilitiesResultSet(0, 1);
    assertThat(minMaxNightRestrictionRule.isApplicable(hotelRate, searchCriteria)).isFalse();

    hotelRate = buildHotelAvailabilitiesResultSet(5, 1);
    assertThat(minMaxNightRestrictionRule.isApplicable(hotelRate, searchCriteria)).isTrue();

    hotelRate = buildHotelAvailabilitiesResultSet(5, 1);
    assertThat(minMaxNightRestrictionRule.isApplicable(hotelRate, searchCriteria)).isTrue();
  }

  @Test
  public void shouldReturnFalseForMinNight5AndMaxNight0() {
    final SearchCriteria searchCriteria = buildSearchCriteria(4);
    RatePlan hotelRate = buildHotelAvailabilitiesResultSet(5, 1);
    assertThat(minMaxNightRestrictionRule.isApplicable(hotelRate, searchCriteria)).isTrue();
  }

  @Test
  public void shouldReturnFalseForMinNight999AndMaxNight3() {
    final SearchCriteria searchCriteria = buildSearchCriteria(4);
    RatePlan hotelRate = buildHotelAvailabilitiesResultSet(5, 1);
    assertThat(minMaxNightRestrictionRule.isApplicable(hotelRate, searchCriteria)).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenRatePlanIsNull() {
    final SearchCriteria searchCriteria = buildSearchCriteria(4);
    assertThat(minMaxNightRestrictionRule.isApplicable(null, searchCriteria)).isFalse();
  }

  private RatePlan buildHotelAvailabilitiesResultSet(final int minNight, final int maxNight) {
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
