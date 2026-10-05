package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.payapp.domain.model.validation.ValidatorFactory;

@Slf4j
public abstract class ModelValidator<T> {

  private final Validator validator = ValidatorFactory.getValidator();

  /**
   * Evaluates all Bean Validations on the attributes of this instance.
   */
  public void validate() {
    if (validator == null) {
      log.warn("The validator is not set!");
      return;
    }
    Set<ConstraintViolation<T>> violations = validator.validate((T) this);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }
}
