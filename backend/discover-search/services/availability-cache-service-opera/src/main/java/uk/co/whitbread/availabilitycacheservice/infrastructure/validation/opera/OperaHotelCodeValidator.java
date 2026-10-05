package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.OperaSearchCriteria;

@Slf4j
public class OperaHotelCodeValidator implements ConstraintValidator<OperaHotelCodeConstraint, OperaSearchCriteria> {

  private static final String A_TO_Z_6_CHARACTERS = "^[A-Z]{6}$";
  private static final Pattern A_TO_Z_6_CHARACTERS_PATTERN = Pattern.compile(A_TO_Z_6_CHARACTERS);

  public static boolean hasOnlyAtoZupperCaseAnd6Characters(final String hotelCode) {
    final Matcher matcher = A_TO_Z_6_CHARACTERS_PATTERN.matcher(hotelCode);
    return matcher.matches();
  }

  @Override
  public void initialize(final OperaHotelCodeConstraint constraintAnnotation) {
    //This validator does not need any initialization
  }

  @Override
  public boolean isValid(final OperaSearchCriteria searchCriteria,
      ConstraintValidatorContext constraintValidatorContext) {
    log.debug("Executing 'HotelCodeValidator' for :{}", searchCriteria);
    return (!(isNull(searchCriteria))
        && isValidHotelCodesPattern(searchCriteria, constraintValidatorContext)
        && isValidHotelCodeSize(searchCriteria, constraintValidatorContext));
  }

  public boolean isNull(final OperaSearchCriteria searchCriteria) {
    log.debug("Validating null inputs for hotel Codes");
    return (searchCriteria == null || searchCriteria.getHotelCodes() == null
        || searchCriteria.getHotelCodes().isEmpty());
  }

  public boolean isValidHotelCodesPattern(final OperaSearchCriteria searchCriteria,
      ConstraintValidatorContext constraintValidatorContext) {
    log.trace("validating for hotel code pattern");
    boolean isValidHotelCodePattern = true;
    for (final String hotelCode : searchCriteria.getHotelCodes()) {
      isValidHotelCodePattern = hasOnlyAtoZupperCaseAnd6Characters(hotelCode);
      if (!isValidHotelCodePattern) {
        final String message = "Invalid hotel codes in input";
        constraintValidatorContext.disableDefaultConstraintViolation(); // disable violation message
        constraintValidatorContext
            .buildConstraintViolationWithTemplate(message)
            .addConstraintViolation();
        return false;
      }
    }
    return isValidHotelCodePattern;
  }

  private boolean isValidHotelCodeSize(final OperaSearchCriteria searchCriteria,
      ConstraintValidatorContext constraintValidatorContext) {
    int hotelSize = searchCriteria.getHotelCodes().size();
    if (hotelSize > 200) {
      final String message = "Hotel Codes List should not contain more than 200 hotel Codes";
      constraintValidatorContext.disableDefaultConstraintViolation(); // disable violation message
      constraintValidatorContext
          .buildConstraintViolationWithTemplate(message)
          .addConstraintViolation();
      return false;
    }
    return true;
  }

}
