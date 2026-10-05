package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import static java.time.temporal.ChronoUnit.DAYS;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class MaxNightsValidator implements ConstraintValidator<MaxNightsDateConstraint, Object> {


  @Value("${hotel.search.default.maxNights}")
  private int defaultMaxNumberOfNights = 14;

  private String arrival;
  private String departure;

  @Override
  public void initialize(MaxNightsDateConstraint constraintAnnotation) {
    this.arrival = constraintAnnotation.arrival();
    this.departure = constraintAnnotation.departure();

  }


  @Override
  public boolean isValid(final Object value, final ConstraintValidatorContext constraintValidatorContext) {
    log.debug("Executing \' MaxNightsValidator \'");
    String arrivalValue = Objects.toString(new BeanWrapperImpl(value).getPropertyValue(arrival), null);
    String departureValue = Objects.toString(new BeanWrapperImpl(value).getPropertyValue(departure), null);
    return validate(arrivalValue, departureValue);
  }

  public boolean validate(final String arrival, final String departure) {
    try {
      if (arrival == null || departure == null) {
        return false;
      }
      long nights = DAYS.between(LocalDate.parse(arrival),
          LocalDate.parse(departure));

      return nights <= defaultMaxNumberOfNights;
    } catch (DateTimeParseException ex) {
      log.error("Date could not be parsed", ex);
      return false;
    }
  }
}
