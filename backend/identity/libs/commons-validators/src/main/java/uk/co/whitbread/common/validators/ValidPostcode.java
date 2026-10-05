package uk.co.whitbread.common.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = PostCodeValidator.class)
@Documented
public @interface ValidPostcode {

    String message() default "Invalid data: Postcode";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}
