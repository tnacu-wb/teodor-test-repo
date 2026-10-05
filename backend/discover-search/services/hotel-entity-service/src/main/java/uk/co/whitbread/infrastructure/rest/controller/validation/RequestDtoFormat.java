package uk.co.whitbread.infrastructure.rest.controller.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = RequestDtoFormatValidator.class)
@Documented
public @interface RequestDtoFormat {

  String message() default "Invalid request parameters";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
