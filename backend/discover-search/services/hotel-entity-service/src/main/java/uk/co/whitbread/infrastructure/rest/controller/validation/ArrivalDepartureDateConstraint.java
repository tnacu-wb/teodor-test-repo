package uk.co.whitbread.infrastructure.rest.controller.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = ArrivalDepartureDateValidator.class)
@Documented
public @interface ArrivalDepartureDateConstraint {
  String message() default "\'startDate/arrivalDate\' may not be on "
      + "or after \'endDate/departureDate\' and earlier than today.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
