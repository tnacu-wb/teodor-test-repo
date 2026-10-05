package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera.distribution;

import static java.lang.annotation.ElementType.TYPE;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.integration.annotation.Payloads;

@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DistributionOccupantsPerRoomValidator.class)
@Documented
public @interface DistributionOccupantsPerRoomConstraint {

  String message() default "Invalid number of adults/children in one of the room/rooms chosen";

  Class<?>[] groups() default {};

  Class<? extends Payloads>[] payload() default {};
}
