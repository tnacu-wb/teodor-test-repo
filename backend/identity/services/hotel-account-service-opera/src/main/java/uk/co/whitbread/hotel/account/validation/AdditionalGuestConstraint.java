package uk.co.whitbread.hotel.account.validation;



import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE, ElementType.PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = AdditionalGuestValidator.class)
@Documented
public @interface AdditionalGuestConstraint {

    String message() default "For each additionalGuest, passport.countryOfIssue length must be of minimum 3 and maximum 25 characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
