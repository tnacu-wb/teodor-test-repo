package uk.co.whitbread.common.validators.password;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordByConfigValidator.class)
public @interface Password {

    String DEFAULT_ERROR_MESSAGE = "is invalid";

    //The key in the configuration file.
    String value();

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String message() default DEFAULT_ERROR_MESSAGE;
}
