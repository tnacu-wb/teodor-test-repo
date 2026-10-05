package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.co.whitbread.availabilitycacheservice.infrastructure.properties.HotelPriceProperties;


public class MaxDateRangeValidatorTest {

  private static final Integer MAX_DAYS = 2;
  private final HotelPriceProperties mockHotelPriceProperties = Mockito.mock(HotelPriceProperties.class);
  private final List<ConstraintValidator<?, ?>> customConstraintValidators =
      Collections.singletonList(new MaxDateRangeValidator(mockHotelPriceProperties));
  private final ValidatorFactory customValidatorFactory =
      new CustomLocalValidatorFactoryBean(customConstraintValidators);
  private final Validator validator = customValidatorFactory.getValidator();
  private final String ERROR_MESSAGE =
      "Arrival/Start date can not be on or after departure/end date and earlier than today." +
          "OR Number of days between arrival/start and departure/end dates is too long, please reduce the gap.";

  @BeforeEach
  public void setUp() throws Exception {

    Mockito.when(mockHotelPriceProperties.getMaxDays())
        .thenReturn(MAX_DAYS);
  }

  @Test
  public void shouldFailIfArrivalAndDepartureDateIsSame() {

    String today = LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();

    MaxDateRangeValidatorTest.DummyClass dc = new MaxDateRangeValidatorTest.DummyClass(today, today);

    Set<ConstraintViolation<MaxDateRangeValidatorTest.DummyClass>> violations = validator.validate(dc);
    MatcherAssert.assertThat("", violations, hasSize(1));
    List<String> messages = getErrorMessages(violations);
    MatcherAssert.assertThat("", messages, containsInAnyOrder(ERROR_MESSAGE));
  }

  @Test
  public void shouldFailIfArrivalDateIsBeforeToday() {

    String yesterday = LocalDate.parse(LocalDate.now().minusDays(1).toString(),
        DateTimeFormatter.ISO_LOCAL_DATE).toString();

    String today = LocalDate.parse(LocalDate.now().toString(),
        DateTimeFormatter.ISO_LOCAL_DATE).toString();

    MaxDateRangeValidatorTest.DummyClass dc = new MaxDateRangeValidatorTest.DummyClass(yesterday, today);

    Set<ConstraintViolation<MaxDateRangeValidatorTest.DummyClass>> violations = validator.validate(dc);
    MatcherAssert.assertThat("", violations, hasSize(1));
    List<String> messages = getErrorMessages(violations);
    MatcherAssert.assertThat("", messages, containsInAnyOrder(ERROR_MESSAGE));
  }

  @Test
  public void shouldFailIfArrivalDateIsAfterDeparture() {

    String arrival = LocalDate.parse(LocalDate.now().plusDays(1).toString(),
        DateTimeFormatter.ISO_LOCAL_DATE).toString();

    String departure = LocalDate.parse(LocalDate.now().toString(),
        DateTimeFormatter.ISO_LOCAL_DATE).toString();

    MaxDateRangeValidatorTest.DummyClass dc = new MaxDateRangeValidatorTest.DummyClass(arrival, departure);
    Set<ConstraintViolation<MaxDateRangeValidatorTest.DummyClass>> violations = validator.validate(dc);
    MatcherAssert.assertThat("", violations, hasSize(1));
    List<String> messages = getErrorMessages(violations);
    MatcherAssert.assertThat("", messages, containsInAnyOrder(ERROR_MESSAGE));
  }

  @Test
  public void ifMaxNightNotExceedThenShouldBeTrue2() {
    String today = LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
    String tomorrow = LocalDate.parse(LocalDate.now().plusDays(1).toString(),
        DateTimeFormatter.ISO_LOCAL_DATE).toString();
    MaxDateRangeValidatorTest.DummyClass dc = new MaxDateRangeValidatorTest.DummyClass(today, tomorrow);
    Set<ConstraintViolation<MaxDateRangeValidatorTest.DummyClass>> violations = validator.validate(dc);
    MatcherAssert.assertThat("", violations, hasSize(0));
  }


  @Test
  public void ifMaxNightExceedThenShouldBeFalse1() {
    String today = LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
    String tomorrow = LocalDate.parse(LocalDate.now().plusDays(2).toString(),
        DateTimeFormatter.ISO_LOCAL_DATE).toString();

    MaxDateRangeValidatorTest.DummyClass dc = new MaxDateRangeValidatorTest.DummyClass(today, tomorrow);
    Set<ConstraintViolation<MaxDateRangeValidatorTest.DummyClass>> violations = validator.validate(dc);
    MatcherAssert.assertThat("", violations, hasSize(1));
    List<String> messages = getErrorMessages(violations);
    MatcherAssert.assertThat("", messages, containsInAnyOrder(ERROR_MESSAGE));
  }

  @Test
  public void ifMaxNightExceedThenShouldBeFalse2() {
    String today = LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
    String tomorrow = LocalDate.parse(LocalDate.now().plusDays(10).toString(),
        DateTimeFormatter.ISO_LOCAL_DATE).toString();

    MaxDateRangeValidatorTest.DummyClass dc = new MaxDateRangeValidatorTest.DummyClass(today, tomorrow);
    Set<ConstraintViolation<MaxDateRangeValidatorTest.DummyClass>> violations = validator.validate(dc);
    MatcherAssert.assertThat("", violations, hasSize(1));
    List<String> messages = getErrorMessages(violations);
    MatcherAssert.assertThat("", messages, containsInAnyOrder(ERROR_MESSAGE));
  }

  private List<String> getErrorMessages(Set<ConstraintViolation<MaxDateRangeValidatorTest.DummyClass>> result) {
    return result.stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
  }

  @MaxDateRangeConstraint(arrival = "arrival", departure = "departure")
  @Data
  @AllArgsConstructor
  private static class DummyClass {

    private String arrival;
    private String departure;

  }

}
