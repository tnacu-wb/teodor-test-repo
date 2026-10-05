package uk.co.whitbread.availabilitycacheservice.infrastructure.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.integration.annotation.Payloads;

@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = PriceFinderSortDateValidator.class)
@Documented
public @interface PriceFinderSortDate {

  String message() default "Sort date may not be outside the searched date range "
      + "- between arrival and (arrival + daysRange).";

  Class<?>[] groups() default {};

  Class<? extends Payloads>[] payload() default {};

}