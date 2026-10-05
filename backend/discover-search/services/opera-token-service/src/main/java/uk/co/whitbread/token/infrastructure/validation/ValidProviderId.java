package uk.co.whitbread.token.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation for provider ID validation. Ensures provider ID contains only alphanumeric characters,
 * hyphens, and underscores.
 */
@Documented
@Constraint(validatedBy = ProviderIdValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidProviderId {

  String message() default "Provider ID must be alphanumeric with hyphens and underscores only";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
