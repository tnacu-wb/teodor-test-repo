package uk.co.whitbread.content.domain.model.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = HotelInformationRequestValidator.class)
public @interface HotelInformationRequestConstraint {

  String message() default "Must specify at least one of the following: `hotelId` or `slug`, but not both.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
