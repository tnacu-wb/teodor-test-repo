package uk.co.whitbread.ohip.domain.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NoEmptyStringsValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoEmptyStrings {
  String message() default "List must not contain empty strings";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
