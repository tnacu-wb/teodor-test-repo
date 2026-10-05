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
@Constraint(validatedBy = DistributionMaxRoomValidator.class)
@Documented
public @interface DistributionMaxRoomConstraint {

  String message() default "Invalid value for room provided as input. If you’d like to book 10 rooms or more, please"
      + " call us and we’ll be happy to help";

  Class<?>[] groups() default {};

  Class<? extends Payloads>[] payload() default {};
}
