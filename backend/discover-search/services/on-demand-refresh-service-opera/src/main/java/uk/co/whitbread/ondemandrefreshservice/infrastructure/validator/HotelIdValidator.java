package uk.co.whitbread.ondemandrefreshservice.infrastructure.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Component
public class HotelIdValidator implements ConstraintValidator<HotelIdConstraint, String> {

  private static final String A_TO_Z_6_CHARACTERS = "^[A-Z]{6}";
  private static final Pattern A_TO_Z_6_CHARACTERS_PATTERN = Pattern.compile(A_TO_Z_6_CHARACTERS);
  private static final String ERROR_MSG_INVALID_HOTEL_ID = "Invalid Hotel Id !";
  private static final String ERROR_MSG_MISSING_HOTEL_IDS = "Missing Hotel Ids !";


  @Override
  public boolean isValid(String hotelId, ConstraintValidatorContext cxt) {
    if (hotelId == null) {
      return false;
    } else {
      return hasOnlyAtoZUpperCaseAnd6Characters(hotelId);
    }
  }

  private boolean hasOnlyAtoZUpperCaseAnd6Characters(String hotelCode) {
    final Matcher matcher = A_TO_Z_6_CHARACTERS_PATTERN.matcher(hotelCode);
    return matcher.matches();
  }


  public String validateHotelIds(Set<String> hotelIdsSet) {

    if (hotelIdsSet.isEmpty()) {
      return ERROR_MSG_MISSING_HOTEL_IDS;
    }

    String errorMessage = null;
    for (String name : hotelIdsSet) {
      if (!isValid(name, null)) {
        errorMessage = ERROR_MSG_INVALID_HOTEL_ID;
        break;
      }
    }
    return errorMessage;
  }
}