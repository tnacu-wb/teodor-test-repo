package uk.co.whitbread.business.tether.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE, ElementType.PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = LinkCodeValidator.class)
@Documented
public @interface LinkCodeConstraint {

    String message() default "Length must be of 11 characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
