package uk.co.whitbread.account.domain.model.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = {})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RUNTIME)
@ReportAsSingleViolation
@Pattern(regexp = "(((\\+\\d{1,2}|00\\d{1,2})[-\\ .]?)?)(\\d[-\\ .]?){5,15}")
public @interface Phone {
  String message() default "not a well-formed telephone/mobile number";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
