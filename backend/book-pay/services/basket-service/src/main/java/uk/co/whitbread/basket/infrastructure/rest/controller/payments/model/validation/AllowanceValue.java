package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static uk.co.whitbread.basket.domain.model.basket.BasketConstant.RANGE_MESSAGE;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = AllowanceValueValidator.class)
@Documented
public @interface AllowanceValue {
  String message() default RANGE_MESSAGE;
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
