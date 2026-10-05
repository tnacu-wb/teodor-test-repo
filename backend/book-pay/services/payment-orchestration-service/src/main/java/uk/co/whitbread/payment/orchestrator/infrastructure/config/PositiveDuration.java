package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Validates that a duration is greater than zero. */
@Documented
@Constraint(validatedBy = PositiveDurationValidator.class)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface PositiveDuration {

  String message() default "must be positive";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
