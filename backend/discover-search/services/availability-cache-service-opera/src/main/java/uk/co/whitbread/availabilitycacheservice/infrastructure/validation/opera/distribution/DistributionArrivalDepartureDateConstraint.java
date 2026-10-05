package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.integration.annotation.Payloads;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = DistributionArrivalDepartureDateValidator.class)
@Documented
public @interface DistributionArrivalDepartureDateConstraint {

  String message() default "arrival date may not be on or after departure date and earlier than today.";

  Class<?>[] groups() default {};

  Class<? extends Payloads>[] payload() default {};

}
