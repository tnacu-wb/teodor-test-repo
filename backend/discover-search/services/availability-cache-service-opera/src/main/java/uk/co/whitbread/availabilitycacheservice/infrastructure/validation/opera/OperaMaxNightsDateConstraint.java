package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.integration.annotation.Payloads;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = OperaMaxNightsValidator.class)
@Documented
public @interface OperaMaxNightsDateConstraint {

  String message() default "Arrival and Departure Dates are too Long";

  Class<?>[] groups() default {};

  Class<? extends Payloads>[] payload() default {};

  String arrival();

  String departure();

}


