package uk.co.whitbread.promo.domain.model.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class DomainValidator<T> {

  private final Validator validator = ValidatorFactory.getValidator();

  /**
   * Evaluates all Bean Validations on the attributes of this instance.
   */
  protected void validateSelf() {
    if (validator == null) {
      log.error("Validator is not set. This is cataloged as DANGEROUS!");
      return;
    }
    Set<ConstraintViolation<T>> violations = validator.validate((T) this);
    if (!violations.isEmpty()) {
      log.error("Validator encountered {} violations", violations.size());
      throw new ConstraintViolationException(violations);
    }
  }
}