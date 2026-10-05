package uk.co.whitbread.content.domain.model.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = HotelsInformationRequestValidator.class)
public @interface HotelsInformationRequestConstraint {

  String message() default "All `hotelId` in the list must be non empty.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
