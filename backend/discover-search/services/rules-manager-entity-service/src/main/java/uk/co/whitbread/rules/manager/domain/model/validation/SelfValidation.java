package uk.co.whitbread.rules.manager.domain.model.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;

public interface SelfValidation<T> {

  default void validateSelf() {
    Validator validator = ValidatorFactory.getValidator();
    Set<ConstraintViolation<T>> violations = validator.validate((T) this);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

}
