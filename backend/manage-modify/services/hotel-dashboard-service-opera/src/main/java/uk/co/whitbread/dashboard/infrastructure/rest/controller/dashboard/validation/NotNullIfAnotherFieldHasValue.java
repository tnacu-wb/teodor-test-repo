package uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.validation;


import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Is valid if all or none fields are empty/null.
 **/
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = NotNullIfAnotherFieldHasValueValidator.class)
@Documented
public @interface NotNullIfAnotherFieldHasValue {

  String message() default "all or none fields must be populated";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String[] values();

  boolean nullable() default false;

}