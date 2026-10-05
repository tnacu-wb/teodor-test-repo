package uk.co.whitbread.content.domain.model.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;

public interface SelfValidation<T> {

  default void validateSelf() {
    Validator validator = ValidatorFactory.getValidator();
    if (validator == null) {
      org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SelfValidation.class);
      log.error("Validator is not set. This is cataloged as DANGEROUS!");
      return;
    }
    Set<ConstraintViolation<T>> violations = validator.validate((T) this);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
  }

}
