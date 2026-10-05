package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = AddNewRoomFormatValidator.class)
@Documented
public @interface AddNewRoomFormat {

  String message() default "Invalid request parameters";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
