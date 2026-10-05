package uk.co.whitbread.hotel.payment.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = TelephoneOrMobilePresentValidator.class)
@Documented
public @interface TelephoneOrMobilePresent {

    String message() default "telephone or mobile number must be present";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
