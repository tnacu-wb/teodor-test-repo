package uk.co.whitbread.infrastructure.rest.controller.groupbooking.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = NotEmptyConditionalValidator.class)
@Documented
public @interface NotEmptyConditional {

  String checkedField();

  String condition();

  String message() default "must not be null";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
