package uk.co.whitbread.rules.agent.infrastructure.rest.controller.validation;

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
import org.springframework.core.annotation.Order;

@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = DateTimeFormatValidator.class)
@Documented
@Order(2)
public @interface DateTimeFormat {

  String message() default "must be in correct date time format";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String value() default "yyyyMMdd'T'HHmmss";
}
