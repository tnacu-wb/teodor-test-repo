package uk.co.whitbread.ohip.infrastructure.rest.client.opera.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = HotelIdValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface HotelIdConstraint {

  String message() default "Invalid Hotel Id";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}


