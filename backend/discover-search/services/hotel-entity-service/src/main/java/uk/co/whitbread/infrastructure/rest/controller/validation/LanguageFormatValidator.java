package uk.co.whitbread.infrastructure.rest.controller.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Locale;
import java.util.Set;

public class LanguageFormatValidator implements ConstraintValidator<LanguageFormat, String> {

  private static final Set<String> ISO_LANGUAGES = Set.of(Locale.getISOLanguages());

  @Override
  public boolean isValid(String language, ConstraintValidatorContext validatorContext) {
    if (language == null) {
      return false;
    }
    return ISO_LANGUAGES.contains(language);
  }

}
