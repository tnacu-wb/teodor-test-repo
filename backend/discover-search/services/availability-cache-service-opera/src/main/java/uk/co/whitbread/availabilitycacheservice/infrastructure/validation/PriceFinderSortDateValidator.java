package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SortingOption;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.pricefinder.model.PriceFinderLocationSearchCriteria;

@Slf4j
public class PriceFinderSortDateValidator implements
    ConstraintValidator<PriceFinderSortDate, PriceFinderLocationSearchCriteria> {

  @Override
  public void initialize(final PriceFinderSortDate constraintAnnotation) {
    // Initialization not required.
  }

  @Override
  public boolean isValid(final PriceFinderLocationSearchCriteria searchCriteria,
      ConstraintValidatorContext constraintValidatorContext) {
    log.debug("Executing \' PriceFinderSortDateValidator \' for :{}", searchCriteria);
    return searchCriteria != null && (!SortingOption.PRICE.equals(searchCriteria.getSortBy()) || StringUtils.isBlank(
        searchCriteria.getSortDate()) || isValidSortDate(searchCriteria));
  }

  public boolean isValidSortDate(final PriceFinderLocationSearchCriteria searchCriteria) {
    try {
      final LocalDate dateRangeStart = LocalDate.parse(searchCriteria.getArrival());
      final LocalDate dateRangeEnd = dateRangeStart.plusDays(searchCriteria.getDaysRange());
      final LocalDate sortDate = LocalDate.parse(searchCriteria.getSortDate());

      if (sortDate.isBefore(dateRangeStart) || sortDate.isAfter(dateRangeEnd)) {
        log.error("Sort date {} must be between arrival date {} and departure date", dateRangeStart, sortDate);
        return false;
      }
      return true;

    } catch (DateTimeParseException ex) {
      log.error("Date could not be parsed", ex);
      return false;
    }
  }
}

