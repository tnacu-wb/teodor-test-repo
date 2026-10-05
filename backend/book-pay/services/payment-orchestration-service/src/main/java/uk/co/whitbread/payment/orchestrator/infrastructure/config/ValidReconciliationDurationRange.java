package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Validates the ordering of the reconciliation duration settings. */
@Documented
@Constraint(validatedBy = ReconciliationDurationRangeValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface ValidReconciliationDurationRange {

  String message() default "max-duration must be greater than or equal to initial-delay";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
