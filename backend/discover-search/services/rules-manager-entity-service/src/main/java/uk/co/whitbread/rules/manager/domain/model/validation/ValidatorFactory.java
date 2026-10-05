package uk.co.whitbread.rules.manager.domain.model.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class ValidatorFactory {

  private static ValidatorFactory instance;
  private static final Validator FALLBACK_VALIDATOR =
      Validation.buildDefaultValidatorFactory().getValidator();
  private Validator validator;

  private ValidatorFactory() {
  }

  public static synchronized ValidatorFactory getInstance(Validator validator) {
    if (instance == null) {
      instance = new ValidatorFactory();
      instance.setValidator(validator);
    }

    return instance;
  }

  public static Validator getValidator() {
    return instance == null ? FALLBACK_VALIDATOR : instance.validator;
  }

  private void setValidator(Validator validator) {
    this.validator = validator;
  }
}
