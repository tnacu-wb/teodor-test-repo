package uk.co.whitbread.availabilitycacheservice.infrastructure.validation.opera;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = OperaDateFormatValidator.class)
@Documented
public @interface OperaDateFormat {

  String message() default "must be in correct date format";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String value() default "yyyy-MM-dd";
}
