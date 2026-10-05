package uk.co.whitbread.ohip.domain.model.validation;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;

import jakarta.validation.Validator;

public class ValidatorFactory {

  private static ValidatorFactory instance;
  private Validator validator;

  private ValidatorFactory() {
  }

  public static void loadDefaultValidator() {
    try (jakarta.validation.ValidatorFactory validatorFactory = buildDefaultValidatorFactory()) {
      getInstance(validatorFactory.getValidator());
    }
  }

  public static synchronized ValidatorFactory getInstance(Validator validator) {
    if (instance == null) {
      instance = new ValidatorFactory();
      instance.setValidator(validator);
    }
    return instance;
  }

  public static Validator getValidator() {
    return instance == null ? null : instance.validator;
  }

  private void setValidator(Validator validator) {
    this.validator = validator;
  }
}
