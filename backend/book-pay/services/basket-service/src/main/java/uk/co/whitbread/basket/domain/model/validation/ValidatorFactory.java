package uk.co.whitbread.basket.domain.model.validation;

import jakarta.validation.Validator;
import java.util.concurrent.atomic.AtomicReference;

public class ValidatorFactory {

  private static final AtomicReference<ValidatorFactory> instance = new AtomicReference<>();
  private final Validator validator;

  private ValidatorFactory(Validator validator) {
    this.validator = validator;
  }

  public static ValidatorFactory getInstance(Validator validator) {
    return instance.updateAndGet(current -> 
        current != null ? current : new ValidatorFactory(validator)
    );
  }

  public static Validator getValidator() {
    ValidatorFactory factory = instance.get();
    return factory == null ? null : factory.validator;
  }
}
