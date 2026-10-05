package uk.co.whitbread.account.domain.model.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = PostcodeConstraintValidator.class)
@Documented
public @interface PostcodeConstraint {

  String message() default "postcode is required for UK and Germany";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };
}
