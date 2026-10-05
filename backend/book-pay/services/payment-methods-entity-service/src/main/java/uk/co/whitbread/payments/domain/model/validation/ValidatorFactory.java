package uk.co.whitbread.payments.domain.model.validation;

import jakarta.validation.Validator;

public class ValidatorFactory {

  private static ValidatorFactory instance;
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
    return instance == null ? null : instance.validator;
  }

  private void setValidator(Validator validator) {
    this.validator = validator;
  }
}
