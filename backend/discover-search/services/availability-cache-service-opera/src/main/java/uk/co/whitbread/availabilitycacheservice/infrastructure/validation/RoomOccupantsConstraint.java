package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import static java.lang.annotation.ElementType.TYPE;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.integration.annotation.Payloads;

@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RoomOccupantsValidator.class)
@Documented
public @interface RoomOccupantsConstraint {

  String message() default "adults, children and type must have an array of length defined by the value in rooms";

  Class<?>[] groups() default {};

  Class<? extends Payloads>[] payload() default {};
}
