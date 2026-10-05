package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;

public class ArrivalDepartureDateValidatorTest {

  private ArrivalDepartureDateValidator arrivalDepartureDateValidator;

  private SearchCriteria criteria;
  private LocalDate today;
  private DateTimeFormatter dtf;

  @BeforeEach
  public void setUp() {
    arrivalDepartureDateValidator = new ArrivalDepartureDateValidator();
    criteria = new SearchCriteria();
    dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    today = LocalDate.now();
  }

  @Test
  public void successfulTestForArrivalDepartureDate() {
    criteria.setArrival(today.format(dtf));
    criteria.setDeparture(today.plusDays(1).format(dtf));
    assertThat(arrivalDepartureDateValidator.isValid(criteria, null)).isTrue();
  }

  @Test
  public void failureTestForNullSearchCriteria() {
    criteria = null;
    assertThat(arrivalDepartureDateValidator.isValid(criteria, null)).isFalse();
  }

  @Test
  public void failureTestForNullArrivalDate() {
    criteria.setArrival(null);
    criteria.setDeparture(today.format(dtf));
    assertThat(arrivalDepartureDateValidator.isValid(criteria, null)).isFalse();

  }

  @Test
  public void failureTestForNullDepartureDate() {
    criteria.setArrival(today.format(dtf));
    criteria.setDeparture(null);
    assertThat(arrivalDepartureDateValidator.isValid(criteria, null)).isFalse();
  }

  @Test
  public void failureTestForNullArrivalAndDepartureDateSearchCriteria() {
    criteria.setArrival(null);
    criteria.setDeparture(null);
    assertThat(arrivalDepartureDateValidator.isValid(criteria, null)).isFalse();
  }

  @Test
  public void failureTestForArrivalBeforeTodaySearchCriteria() {
    criteria.setArrival(today.minusDays(1).format(dtf));
    criteria.setDeparture(null);
    assertThat(arrivalDepartureDateValidator.isValid(criteria, null)).isFalse();
  }

  @Test
  public void failureTestForArrivalAndDepartureEqualsSearchCriteria() {
    criteria.setArrival(today.format(dtf));
    criteria.setDeparture(today.format(dtf));
    assertThat(arrivalDepartureDateValidator.isValid(criteria, null)).isFalse();
  }

  @Test
  public void failureTestForArrivalAfterDepartureSearchCriteria() {
    criteria.setArrival(today.plusDays(3).format(dtf));
    criteria.setDeparture(today.plusDays(1).format(dtf));
    assertThat(arrivalDepartureDateValidator.isValid(criteria, null)).isFalse();
  }

  @Test
  public void failureTestForArrivalWithInvalidDateSearchCriteria() {
    criteria.setArrival("invalid");
    criteria.setDeparture(today.plusDays(1).format(dtf));
    assertThat(arrivalDepartureDateValidator.isValid(criteria, null)).isFalse();
  }

  @Test
  public void failureTestForDepartureWithInvalidDateSearchCriteria() {
    criteria.setArrival(today.plusDays(1).format(dtf));
    criteria.setDeparture("invalid");
    assertThat(arrivalDepartureDateValidator.isValid(criteria, null)).isFalse();
  }

}
