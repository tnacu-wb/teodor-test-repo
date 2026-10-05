package uk.co.whitbread.reservation.domain.model.validator;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {HotelAvailabilityRequestDatesValidator.class,
    HotelAvailabilityByIdsRequestDatesValidator.class,
    HotelAvailabilityByIdsV2RequestDatesValidator.class})
public @interface ValidAvailabilityDates {

  String message() default "Invalid arrival and/or departure dates!";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}