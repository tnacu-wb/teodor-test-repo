package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import static java.time.temporal.ChronoUnit.DAYS;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapperImpl;
import uk.co.whitbread.availabilitycacheservice.infrastructure.properties.HotelPriceProperties;

@Slf4j
@RequiredArgsConstructor
public class MaxDateRangeValidator implements ConstraintValidator<MaxDateRangeConstraint, Object> {

  private final HotelPriceProperties hotelPriceProperties;
  private String arrival;
  private String departure;

  private boolean isValidArrivalAndDepartureDates(final Object searchCriteria) {

    final String arr = Objects.toString(new BeanWrapperImpl(searchCriteria).getPropertyValue(arrival));
    final String dep = Objects.toString(new BeanWrapperImpl(searchCriteria).getPropertyValue(departure));

    try {
      log.trace("Validating arrival date is before the departure date.... arrival - {} Departure - {}", arr, dep);

      final LocalDate startDate = LocalDate.parse(arr, DateTimeFormatter.ISO_LOCAL_DATE);
      final LocalDate endDate = LocalDate.parse(dep, DateTimeFormatter.ISO_LOCAL_DATE);

      if (startDate.isBefore(LocalDate.now())
          || startDate.equals(endDate)
          || startDate.isAfter(endDate)) {
        log.error("arrival date check failed, Arrival Date {} and Departure Date {}", startDate, endDate);
        return false;
      }

      final int maxDays = hotelPriceProperties.getMaxDays();
      log.debug("maxDays: {}", maxDays);
      return DAYS.between(startDate, endDate) < maxDays;

    } catch (DateTimeParseException ex) {
      log.error("Date could not be parsed", ex);
      return false;
    }
  }

  @Override
  public void initialize(final MaxDateRangeConstraint constraintAnnotation) {
    this.arrival = constraintAnnotation.arrival();
    this.departure = constraintAnnotation.departure();
  }

  @Override
  public boolean isValid(Object searchCriteria,
      ConstraintValidatorContext constraintValidatorContext) {
    log.debug("Executing \' MaxDateRangeValidator \' for :{}", searchCriteria);
    return (!(isNull(searchCriteria)) && isValidArrivalAndDepartureDates(searchCriteria));
  }

  private boolean isNull(final Object searchCriteria) {

    final String startDate = Objects.toString(new BeanWrapperImpl(searchCriteria).getPropertyValue(arrival));
    final String endDate = Objects.toString(new BeanWrapperImpl(searchCriteria).getPropertyValue(departure));

    return (searchCriteria == null || startDate == null || endDate == null);
  }
}
