package uk.co.whitbread.ondemandrefreshservice.infrastructure.validator;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HotelIdValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface HotelIdConstraint {

  String message() default "Invalid Hotel Id";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}


