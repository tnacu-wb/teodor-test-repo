package uk.co.whitbread.shared.commons.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.shared.commons.validation.enums.ValidationError;
import uk.co.whitbread.shared.commons.validation.exceptions.BadRequestValidationException;

@Component
@Slf4j
public class CompanyNameValidator implements ConstraintValidator<CompanyName, String> {

  @Autowired
  private CompanyNameSwitch companyNameSwitch;

  private static final String SPECIAL_CHARACTERS = "\\u005B\\u005D\\\\/\\u00AB\\u00BB\\u2018\\u2019\\u201C\\u201D"
      + ".,:;_!?\"*%=+£$€¥&@#(){}<>\\-'";
  /**
   * Regex to validate company name.
   * The regex allows for a wide range of characters, including letters (both uppercase and lowercase),
   * numbers, spaces, and various special characters.
   * It also includes support for accented characters and some punctuation marks.
   */
  private static final String COMPANY_NAME_REGEX = "^[a-zÀÁÂÃÄÅĀẶĄẮÆǼẞÇĆĈĊČĎĐÈÉÊËĒĔĖĚĜĞĠĢĤĦÌÍÎÏĨĪǏĮİ"
      + "ĴĶĹĻĽĿŁÑŃŅŇȠÒÓÔÕÖØŌǑŐǾŒṘŖŘŚŜṢŠŢŤŦÙÚÛÜŨŪǓŮŰŲŴẂẀẄỲÝŸŶŹŻŽàáâãäåāặąắæǽßçćĉċčďđèéêëēĕėěĝğġģĥħ"
      + "ìíîïĩīǐįĵķĺļľŀłñńņňƞòóôõöøōǒőǿœṙŗřśŝṣšţťŧùúûüũūǔůűųŵẃẁẅỳýÿŷźżž"
      + SPECIAL_CHARACTERS + "0-9 ]+$";

  @Override
  public void initialize(CompanyName constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
    if (companyNameSwitch == null || !companyNameSwitch.isEnabled() || name == null || name.isEmpty()) {
      return true;
    }
    Pattern pattern = Pattern.compile(COMPANY_NAME_REGEX, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(name);
    log.debug("Validating company name {} against regex: {}", name, COMPANY_NAME_REGEX);
    if (matcher.matches()) {
      return true;
    }
    var message = String.format("Invalid company name %s. Valid characters are letters, numbers, "
        + "space and various special characters: %s", name, regexToString(SPECIAL_CHARACTERS));
    return throwBadRequest(message);
  }

  private static boolean throwBadRequest(String message) {
    var ex = new BadRequestValidationException(ValidationError.DIGITAL_INVALID_COMPANY_NAME_EXCEPTION,
        message);
    ExceptionLogger.log(log, ex);
    throw ex;
  }

  private static String regexToString(String input) {
    Pattern unicodePattern = Pattern.compile("\\\\u([0-9A-Fa-f]{4})");
    Matcher matcher = unicodePattern.matcher(input);
    StringBuilder decodedString = new StringBuilder();
    while (matcher.find()) {
      String unicodeChar = String.valueOf((char) Integer.parseInt(matcher.group(1), 16));
      matcher.appendReplacement(decodedString, unicodeChar);
    }
    matcher.appendTail(decodedString);
    var decodedInput = decodedString.toString();
    return decodedInput
        .replace("\\\\", "\\")
        .replace("\\-", "-")
        .replace("\\\"", "\"");
  }
}
