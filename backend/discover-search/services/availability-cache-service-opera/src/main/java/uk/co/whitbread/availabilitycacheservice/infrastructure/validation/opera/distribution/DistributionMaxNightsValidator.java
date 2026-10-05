package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution;

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
public class DistributionMaxNightsValidator implements
    ConstraintValidator<DistributionMaxNightsDateConstraint, Object> {


  @Value("${hotel.search.distribution.maxNights}")
  private int defaultMaxNumberOfNights;

  private String arrival;
  private String departure;

  @Override
  public void initialize(DistributionMaxNightsDateConstraint constraintAnnotation) {
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
