package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.integration.annotation.Payloads;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = RoomTypeValidator.class)
@Documented
public @interface RoomTypeConstraint {

  String message() default "Invalid roomType";

  Class<?>[] groups() default {};

  Class<? extends Payloads>[] payload() default {};

  String roomType();

}
