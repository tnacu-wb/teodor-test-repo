package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.gqt;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.integration.annotation.Payloads;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = GqtArrivalDepartureDateValidator.class)
@Documented
public @interface GqtArrivalDepartureDateConstraint {

  String message() default "arrival date may not be on or after departure date and earlier than today.";

  Class<?>[] groups() default {};

  Class<? extends Payloads>[] payload() default {};

}
