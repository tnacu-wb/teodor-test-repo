package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation;

import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.COUNTRY_CODE_DE;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.COUNTRY_CODE_GB;
import static uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants.DISTRIBUTION_CHANNEL;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.AddressDto;

public class ValidBillingAddressValidator implements
    ConstraintValidator<ValidBillingAddress, AddressDto> {

  private static final String DE_POSTCODE_PATTERN = "^([0123456789]\\d{4})$";
  private static final String GB_POSTCODE_PATTERN =
      "^([A-Za-z][A-HJ-Ya-hj-y]?\\d[A-Za-z\\d]? ?\\d[A-Za-z]{2}|GIR ?0A{2})$";
  public static final String INVALID_POSTCODE = "Invalid postcode";
  public static final String NULL_POSTCODE = "Postcode is null";
  public static final String POSTCODE_LONG = "Size must be between 1 and 12";
  public static final String MANDATORY_LOCATION = "Location is mandatory";
  public static final String SPECIAL_CHARACTERS = "Must not contain special characters";
  public static final String LENGTH_INTERVAL = "Size must be between 1 and 100";

  @Override
  public void initialize(ValidBillingAddress constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(AddressDto addressDto,
      ConstraintValidatorContext context) {
    Pattern pattern;
    Matcher matcher;
    var postalCode = addressDto.getPostalCode();
    int postalCodeLength;
    boolean result;

    if (DISTRIBUTION_CHANNEL.equals(addressDto.getChannel())) {
      return true;
    }

    result = checkNullPostCode(addressDto, context);

    if (addressDto.getPostalCode() != null) {
      postalCodeLength = addressDto.getPostalCode().length();
      result = isValidLength(context, postalCodeLength);
    }

    var country = addressDto.getCountry();
    if (COUNTRY_CODE_GB.equals(country) && addressDto.getPostalCode() != null) {
      postalCodeLength = addressDto.getPostalCode().length();
      if (postalCodeLength >= 5 && postalCodeLength <= 8) {
        pattern = Pattern.compile(GB_POSTCODE_PATTERN);
        matcher = pattern.matcher(postalCode);
        result = isValidPattern(matcher, context);
      } else {
        result = simpleErrorMessage(context, INVALID_POSTCODE);
      }
    } else if (COUNTRY_CODE_DE.equals(country) && addressDto.getPostalCode() != null) {
      postalCodeLength = addressDto.getPostalCode().length();
      if (postalCodeLength >= 1 && postalCodeLength <= 5) {
        pattern = Pattern.compile(DE_POSTCODE_PATTERN);
        matcher = pattern.matcher(postalCode);
        result = isValidPattern(matcher, context);
      } else {
        result = simpleErrorMessage(context, INVALID_POSTCODE);
      }
    }
    return result;
  }

  private boolean isValidLength(ConstraintValidatorContext context, Integer postalCodeLength) {
    if (postalCodeLength > 12) {
      return simpleErrorMessage(context, POSTCODE_LONG);
    }
    return true;
  }

  private boolean checkNullPostCode(AddressDto addressDto, ConstraintValidatorContext context) {
    return addressDto.getPostalCode() == null ? simpleErrorMessage(context, NULL_POSTCODE) : Boolean.TRUE;
  }

  private boolean isValidPattern(Matcher matcher, ConstraintValidatorContext context) {
    if (!matcher.matches()) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(INVALID_POSTCODE)
          .addConstraintViolation();
      return false;
    }
    return true;
  }

  private boolean simpleErrorMessage(ConstraintValidatorContext context, String message) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(message)
        .addConstraintViolation();
    return false;
  }
}
