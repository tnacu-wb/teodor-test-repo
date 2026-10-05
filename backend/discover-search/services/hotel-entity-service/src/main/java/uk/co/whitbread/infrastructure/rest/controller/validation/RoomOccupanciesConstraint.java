package uk.co.whitbread.infrastructure.rest.controller.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {RoomOccupanciesValidator.class})
@Documented
public @interface RoomOccupanciesConstraint {

  String message() default "adultsNumber and childrenNumber must be the same size";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
