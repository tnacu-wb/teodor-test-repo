package uk.co.whitbread.company.utils;

import java.util.List;
import lombok.experimental.UtilityClass;
import uk.co.whitbread.company.exceptions.InvalidEmailException;

@UtilityClass
public class EmailValidator {

  private static final String EMAIL_REGEX = "^[\\w-.]+@([\\w-]+\\.){1,20}+[\\w-]{2,4}$";

  public static void validateEmails(List<String> recipientEmailAddresses) {
    for (String email : recipientEmailAddresses) {
      if (!email.matches(EMAIL_REGEX)) {
        throw new InvalidEmailException("Email address is invalid");
      }
    }
  }
}
