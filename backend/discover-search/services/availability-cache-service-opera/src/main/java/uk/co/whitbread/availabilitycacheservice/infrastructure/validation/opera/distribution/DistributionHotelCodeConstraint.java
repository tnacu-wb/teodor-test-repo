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
@Constraint(validatedBy = DistributionHotelCodeValidator.class)
@Documented
public @interface DistributionHotelCodeConstraint {

  String message() default "Invalid HotelCodes provided as input.";

  Class<?>[] groups() default {};

  Class<? extends Payloads>[] payload() default {};
}
