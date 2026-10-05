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
@Constraint(validatedBy = ArrivalDepartureDateValidator.class)
@Documented
public @interface ArrivalDepartureDateConstraint {

  String message() default "arrival date may not be on or after departure date and earlier than today.";

  Class<?>[] groups() default {};

  Class<? extends Payloads>[] payload() default {};

}
