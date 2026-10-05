package uk.co.whitbread.rules.agent.infrastructure.rest.controller.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = AmendmentRuleDtoRequestFormatValidator.class)
@Documented
public @interface AmendmentRuleDtoRequestFormat {

  String message() default "Invalid request parameters";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
