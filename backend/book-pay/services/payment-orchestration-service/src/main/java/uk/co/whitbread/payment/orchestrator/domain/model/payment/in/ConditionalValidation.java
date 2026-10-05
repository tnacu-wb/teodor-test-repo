package uk.co.whitbread.payment.orchestrator.domain.model.payment.in;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Class-level validation constraint for {@link PaymentInitRequest} subtypes.
 *
 * <p>Applies conditional validation rules based on the concrete request type.
 * For {@link NewCardWebInitRequest}, validates that the returnUrl uses HTTPS
 * and belongs to the configured host allowlist.
 *
 * <p>This must be a class-level ({@code @Target(TYPE)}) annotation because
 * Bean Validation cannot apply a custom ConstraintValidator to individual fields
 * on a sealed interface hierarchy without triggering {@code UnexpectedTypeException}.
 */
@Documented
@Constraint(validatedBy = ConditionalValidationValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface ConditionalValidation {

  String message() default "Request validation failed";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
