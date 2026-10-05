package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera;

import static java.lang.annotation.ElementType.TYPE;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.integration.annotation.Payloads;

@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OperaOccupantsPerRoomValidator.class)
@Documented
public @interface OperaOccupantsPerRoomConstraint {

  String message() default "Invalid number of adults/children in one of the room/rooms chosen";

  Class<?>[] groups() default {};

  Class<? extends Payloads>[] payload() default {};
}
