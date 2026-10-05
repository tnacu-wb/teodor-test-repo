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
@Constraint(validatedBy = GqtHotelCodeValidator.class)
@Documented
public @interface GqtHotelCodeConstraint {

  String message() default "Invalid HotelCodes provided as input or Page/Size value is less than or equals to 0";

  Class<?>[] groups() default {};

  Class<? extends Payloads>[] payload() default {};
}
