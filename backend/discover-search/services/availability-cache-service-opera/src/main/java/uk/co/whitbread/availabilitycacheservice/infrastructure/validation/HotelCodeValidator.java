package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.SortType;

@Slf4j
public class HotelCodeValidator implements ConstraintValidator<HotelCodeConstraint, SearchCriteria> {

  private static final String A_TO_Z_6_CHARACTERS = "^[A-Z]{6}$";
  private static final Pattern A_TO_Z_6_CHARACTERS_PATTERN = Pattern.compile(A_TO_Z_6_CHARACTERS);

  public static boolean hasOnlyAtoZupperCaseAnd6Characters(final String hotelCode) {
    final Matcher matcher = A_TO_Z_6_CHARACTERS_PATTERN.matcher(hotelCode);
    return matcher.matches();
  }

  @Override
  public void initialize(final HotelCodeConstraint constraintAnnotation) {
    //This validator does not need any initialization
  }

  @Override
  public boolean isValid(final SearchCriteria searchCriteria, ConstraintValidatorContext constraintValidatorContext) {
    log.debug("Executing 'HotelCodeValidator' for :{}", searchCriteria);
    return (!(isNull(searchCriteria)) && isValidHotelCodeSizeWithPageAndSize(searchCriteria, constraintValidatorContext)
        && isValidPageAndSizeWithSortPrice(searchCriteria, constraintValidatorContext)
        && isValidHotelCodesPattern(searchCriteria, constraintValidatorContext));
  }

  public boolean isNull(final SearchCriteria searchCriteria) {
    log.trace("Validating null inputs for hotel Codes and 0 values passed on Page and Size....");
    return (searchCriteria == null || searchCriteria.getHotelCodes() == null
        || searchCriteria.getHotelCodes().isEmpty()
        || searchCriteria.getPage() < 0 || searchCriteria.getSize() < 0);
  }

  public boolean isValidHotelCodeSizeWithPageAndSize(final SearchCriteria searchCriteria,
      ConstraintValidatorContext constraintValidatorContext) {
    final int hotelCodeSize = searchCriteria.getHotelCodes().size();
    boolean isValid = true;
    String errorMessage = null;
    if (searchCriteria.getSort() == SortType.DISTANCE) {
      if (searchCriteria.getPage() <= 1 && hotelCodeSize > 40) {
        errorMessage = "Hotel Codes List should not contain more than 40 hotel Codes when page is 1 for Sort Type is"
            + " DISTANCE";
        isValid = false;
      } else if (searchCriteria.getPage() > 1 && hotelCodeSize > 10) {
        errorMessage = "Hotel Codes List should not contain more than 10 hotel Codes when Page Value is greater than"
            + " 1 for Sort Type is DISTANCE";
        isValid = false;
      }
    } else if (searchCriteria.getSort() == SortType.PRICE) {
      if (hotelCodeSize < 1) {
        errorMessage = "At least one hotelCode is required on searchCriteria when SortType is PRICE";
        isValid = false;
      }
    } else {
      errorMessage = "Invalid Sort Type. Sort Type should be either DISTANCE or PRICE.";
      isValid = false;
    }
    if (!isValid) {
      setErrorMessage(constraintValidatorContext, errorMessage);
    }
    return isValid;
  }

  public boolean isValidPageAndSizeWithSortPrice(final SearchCriteria searchCriteria,
      ConstraintValidatorContext constraintValidatorContext) {
    boolean isValid = true;
    String errorMessage = null;
    if (searchCriteria.getSort() == SortType.PRICE) {
      if (searchCriteria.getPage() < 1 || searchCriteria.getSize() < 1) {
        errorMessage = "page/size cannot be empty or zero when sort = PRICE";
        isValid = false;
      } else if (searchCriteria.getPage() == 1 && searchCriteria.getSize() != 40) {
        errorMessage = "size should be equal to 40 for page = 1, sort = PRICE";
        isValid = false;
      } else if (searchCriteria.getPage() > 1 && searchCriteria.getSize() != 10) {
        errorMessage = "size should be equal to 10 for page > 1, sort = PRICE";
        isValid = false;
      }
    }
    if (!isValid) {
      setErrorMessage(constraintValidatorContext, errorMessage);
    }
    return isValid;
  }

  public boolean isValidHotelCodesPattern(final SearchCriteria searchCriteria,
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

  private ConstraintValidatorContext setErrorMessage(ConstraintValidatorContext constraintValidatorContext,
      String message) {
    constraintValidatorContext.disableDefaultConstraintViolation(); // disable violation message
    constraintValidatorContext
        .buildConstraintViolationWithTemplate(message)
        .addConstraintViolation();
    return constraintValidatorContext;
  }

}
