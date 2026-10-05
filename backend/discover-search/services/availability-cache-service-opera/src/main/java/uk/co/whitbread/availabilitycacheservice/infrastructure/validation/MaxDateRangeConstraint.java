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
@Constraint(validatedBy = MaxDateRangeValidator.class)
@Documented
public @interface MaxDateRangeConstraint {

  String message() default "Arrival/Start date can not be on or after departure/end date and earlier than today."
      + "OR Number of days between arrival/start and departure/end dates is too long, please reduce the gap.";

  Class<?>[] groups() default {};

  Class<? extends Payloads>[] payload() default {};

  String arrival();

  String departure();

}


